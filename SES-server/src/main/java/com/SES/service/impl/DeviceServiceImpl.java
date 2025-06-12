package com.SES.service.impl;

import com.SES.config.RabbitMQConfig;
import com.SES.constant.DeviceStatusConstant;
import com.SES.context.BaseContext;
import com.SES.dto.device.*;
import com.SES.dto.deviceApi.DeviceInitApiResultDTO;
import com.SES.dto.logCommonCache.RefreshLogCommonCacheDeviceMessageDTO;
import com.SES.dto.logCommonCache.RefreshLogCommonCachePolicyMessageDTO;
import com.SES.dto.logCommonCache.RefreshLogCommonCacheMessageDTO;
import com.SES.entity.*;
import com.SES.exception.BaseException;
import com.SES.mapper.*;
import java.util.List;
import com.SES.result.PageResult;
import com.SES.service.DeviceApiService;
import com.SES.service.DeviceService;
import com.SES.utils.StatusCodeValidator;
import com.SES.vo.device.DeviceModeVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private PolicyItemMapper policyItemMapper;

    @Autowired
    private SimDeviceMapper simDeviceMapper;

    @Autowired
    private SimDeviceModeMapper simDeviceModeMapper;

    @Autowired
    private DeviceModeMapper deviceModeMapper;

    @Autowired
    private DeviceApiService deviceApiService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 新增设备
     * @param deviceDTO
     */
    @Override
    public void addDevice(DeviceDTO deviceDTO) {
        Long currentUserId = BaseContext.getCurrentId();
        log.info("当前用户ID：{}", currentUserId);

        if (currentUserId == null) {
            throw new BaseException("用户未登录或token无效");
        }

        Device device = new Device();
        device.setUserId(currentUserId);
        device.setName(deviceDTO.getName());
        device.setLastKnownStatus(DeviceStatusConstant.OFF); // 默认状态为关闭
        deviceMapper.insert(device);

        // 根据设备类型初始化设备
        Long deviceId = device.getId(); // 插入后自动写回主键，供这里使用
        DeviceInitApiResultDTO deviceInitApiResultDTO = deviceApiService.deviceInitApi(deviceId, deviceDTO.getType());

        // 插入模式表
        List<String>  modeList = deviceInitApiResultDTO.getModeList();
        for (String modeName : modeList) {
            DeviceMode deviceMode = new DeviceMode();
            deviceMode.setDeviceId(deviceId);
            deviceMode.setName(modeName);

            deviceModeMapper.insert(deviceMode);
        }

        // 回填默认模式名
        device.setDefaultModeName(deviceInitApiResultDTO.getDefaultModeName());
        deviceMapper.update(device);

        // 通知刷新DeviceId缓存
        rabbitTemplate.convertAndSend("deviceExchange", "device.cache.refresh", "refresh");


        // 通知新建LogCommon缓存
        try {
            RefreshLogCommonCacheMessageDTO dto = new RefreshLogCommonCacheMessageDTO();
            dto.setDeviceId(deviceId);

            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_LOG_COMMON_REFRESH, dto);
            log.info("已发送设备缓存刷新消息: {}", dto);
        } catch (Exception e) {
            log.error("发送设备缓存刷新消息失败", e);


            log.info("用户{}新增设备成功：{}", currentUserId, deviceDTO.getName());
        }
    }

    /**
     * 删除设备
     * @param id
     */
    @Override
    public void deleteDevice(Long id) {
        Long currentUserId = BaseContext.getCurrentId();

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(id, currentUserId);
        if (device == null) {
            throw new BaseException("设备不存在或无权限操作");
        }


        // 级联删除相关数据
        // 1. 删除设备的所有策略条目
        List<Policy> policies = policyMapper.getByDeviceId(id);
        for (Policy policy : policies) {
            policyItemMapper.deleteByPolicyId(policy.getId());
        }

        // 2. 删除设备的所有策略
        policyMapper.deleteByDeviceId(id);

        // 3. 删除设备的所有模式
        deviceModeMapper.deleteByDeviceId(id);

        // 4. 删除模拟设备相关数据
        simDeviceMapper.deleteByDeviceId(id);
        simDeviceModeMapper.deleteByDeviceId(id);

        // 5. 最后删除设备本身
        deviceMapper.deleteById(id);

        // 发送消息通知刷新缓存
        rabbitTemplate.convertAndSend("deviceExchange", "device.cache.refresh", "refresh");

        log.info("用户{}删除设备：{}", currentUserId, device.getName());
    }

    /**
     * 分页查询设备
     * @param devicePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DevicePageQueryDTO devicePageQueryDTO) {
        Long currentUserId = BaseContext.getCurrentId();
        
        // 设置当前用户ID到查询条件中
        devicePageQueryDTO.setUserId(currentUserId);
        
        PageHelper.startPage(devicePageQueryDTO.getPage(), devicePageQueryDTO.getPageSize());
        
        Page<Device> page = deviceMapper.pageQuery(devicePageQueryDTO);
        
        long total = page.getTotal();
        List<Device> records = page.getResult();
        
        return new PageResult(total, records);
    }

    /**
     * 根据id查询设备模式
     * @param id
     * @return
     */
    @Override
    public List<DeviceModeVO> getModeByDeviceId(Long id) {
        Long currentUserId = BaseContext.getCurrentId();

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(id, currentUserId);
        if (device == null) {
            throw new BaseException("设备不存在或无权限操作");
        }

        return deviceModeMapper.getVOByDeviceId(device.getId());
    }

    /**
     * 修改设备名称
     * @param id
     * @param deviceNameEditDTO
     */
    @Override
    public void editDeviceName(Long id, DeviceNameEditDTO deviceNameEditDTO) {
        Long currentUserId = BaseContext.getCurrentId();

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(id, currentUserId);
        if (device == null) {
            throw new BaseException("设备不存在或无权限操作");
        }

        // 获取旧名称用于日志记录
        String oldName = device.getName();
        String newName = deviceNameEditDTO.getName();

        // 更新设备名称
        device.setName(newName);
        deviceMapper.update(device);

        // 构造并发送消息
        RefreshLogCommonCacheDeviceMessageDTO dto = new RefreshLogCommonCacheDeviceMessageDTO();
        dto.setDeviceId(id);
        dto.setDeviceName(newName);

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_LOG_COMMON_REFRESH_DEVICE, dto);
            log.info("已发送设备名称变更消息: deviceId={}, deviceName={}", dto.getDeviceId(), dto.getDeviceName());
        } catch (Exception e) {
            log.error("发送设备名称变更消息失败", e);
        }

        log.info("用户{}修改设备{}名称为：{}", currentUserId, id, newName);
    }

    /**
     * 修改设备策略
     * @param id
     * @param devicePolicyEditDTO
     */
    @Override
    public void editDevicePolicy(Long id, DevicePolicyEditDTO devicePolicyEditDTO) {
        Long currentUserId = BaseContext.getCurrentId();

        // 验证策略应用状态码
        StatusCodeValidator.validatePolicyApplyStatus(devicePolicyEditDTO.getIsApplyPolicy());

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(id, currentUserId);
        if (device == null) {
            throw new BaseException("设备不存在或无权限操作");
        }

        if (devicePolicyEditDTO.getIsApplyPolicy() == 0) {
            // 解绑策略
            device.setPolicyId(null);
        } else if (devicePolicyEditDTO.getIsApplyPolicy() == 1) {
            // 应用策略
            Long policyId = devicePolicyEditDTO.getPolicyId();
            if (policyId != null) {
                // 验证：该策略id的策略，必须属于当前设备
                Policy policy = policyMapper.getById(policyId);
                if (policy == null) {
                    throw new BaseException("策略不存在");
                }
                if (!policy.getDeviceId().equals(id)) {
                    throw new BaseException("策略不属于当前设备，无法应用");
                }
            }
            device.setPolicyId(policyId);
        }
        deviceMapper.update(device);
        
        // 记录操作日志
        // 跳过

        // 构造并发送消息
        try {
            RefreshLogCommonCachePolicyMessageDTO dto = new RefreshLogCommonCachePolicyMessageDTO();
            dto.setDeviceId(id);
            dto.setPolicyId(device.getPolicyId()); // 可能为 null，表示解绑策略

            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_POLICY_MONITOR_REFRESH, id);
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_LOG_COMMON_REFRESH_POLICY, dto);

            log.info("已发送设备策略变更消息: {}", dto);
        } catch (Exception e) {
            log.error("发送设备策略变更消息失败", e);
        }
        
        log.info("用户{}修改设备{}策略", currentUserId, id);
    }

    /**
     * 控制设备运行状态
     * @param id
     * @param deviceStatusEditDTO
     */
    @Override
    public void editDeviceStatus(Long id, DeviceStatusEditDTO deviceStatusEditDTO) {
        Long currentUserId = BaseContext.getCurrentId();

        // 验证设备状态码
        StatusCodeValidator.validateDeviceStatus(deviceStatusEditDTO.getStatus());

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(id, currentUserId);
        if (device == null) {
            throw new BaseException("设备不存在或无权限操作");
        }

        Integer status = deviceStatusEditDTO.getStatus();
        // 如果开机，设置为默认模式
        String modeName = (status == DeviceStatusConstant.ON)
                ? device.getDefaultModeName()
                : null;

        // 调用设备控制Api
        deviceApiService.deviceControlApi(id,status,modeName);

        // 记录操作日志
        // 跳过
        
        log.info("用户{}控制设备{}状态为：{}", currentUserId, id, deviceStatusEditDTO.getStatus());
    }

    /**
     * 控制设备模式
     * @param id
     * @param deviceModeEditDTO
     */
    @Override
    public void editDeviceMode(Long id, DeviceModeEditDTO deviceModeEditDTO) {
        Long currentUserId = BaseContext.getCurrentId();

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(id, currentUserId);
        if (device == null) {
            throw new BaseException("设备不存在或无权限操作");
        }

        // 获取模式名
        DeviceMode deviceMode = deviceModeMapper.getById(deviceModeEditDTO.getModeId());
        if (deviceMode == null) {
            throw new BaseException("设备模式不存在");
        }
        String modeName = deviceMode.getName();

        // 调用设备控制Api
        deviceApiService.deviceControlApi(id, null,modeName);

        // 记录操作日志
        // 跳过
        
        log.info("用户{}控制设备{}模式为：{}", currentUserId, id, deviceModeEditDTO.getModeId());
    }

    /**
     * 综合控制设备
     * @param id
     * @param deviceControlDTO
     */
    @Override
    public void editDevice(Long id, DeviceControlDTO deviceControlDTO) {
        Long currentUserId = BaseContext.getCurrentId();

        // 验证状态码
        StatusCodeValidator.validateDeviceStatus(deviceControlDTO.getStatus());
        StatusCodeValidator.validatePolicyApplyStatus(deviceControlDTO.getIsApplyPolicy());

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(id, currentUserId);
        if (device == null) {
            throw new BaseException("设备不存在或无权限操作");
        }

        Long oldPolicyId = device.getPolicyId(); // 保存旧策略ID用于判断是否有变化

        // 处理策略
        if (deviceControlDTO.getIsApplyPolicy() != null) {
            if (deviceControlDTO.getIsApplyPolicy() == 0) {
                // 解绑策略
                device.setPolicyId(null);
            } else if (deviceControlDTO.getIsApplyPolicy() == 1) {
                // 应用策略
                Long policyId = deviceControlDTO.getPolicyId();
                if (policyId != null) {
                    // 验证：该策略id的策略，必须属于当前设备
                    Policy policy = policyMapper.getById(policyId);
                    if (policy == null) {
                        throw new BaseException("策略不存在");
                    }
                    if (!policy.getDeviceId().equals(id)) {
                        throw new BaseException("策略不属于当前设备，无法应用");
                    }
                }
                device.setPolicyId(policyId);
            }
            deviceMapper.update(device);


        }


        // 处理状态和模式
        Integer status = deviceControlDTO.getStatus();
        String modeName = null;

        // 获取模式名
        Long modeId = deviceControlDTO.getModeId();
        if (modeId != null) {
            DeviceMode deviceMode = deviceModeMapper.getById(deviceControlDTO.getModeId());
            if (deviceMode == null) {
                throw new BaseException("设备模式不存在");
            }
            modeName = deviceMode.getName();
        }
        else if (status == DeviceStatusConstant.ON) {
            // 如果开机，设置为默认模式
            modeName = device.getDefaultModeName();
        }

        // 调用设备控制Api
        deviceApiService.deviceControlApi(id,status,modeName);
        
        // 记录操作日志
        // 跳过

        // 如果策略变更，发送消息
        if (deviceControlDTO.getIsApplyPolicy() != null
                && !Objects.equals(oldPolicyId, device.getPolicyId())) {
            try {
                RefreshLogCommonCachePolicyMessageDTO dto = new RefreshLogCommonCachePolicyMessageDTO();
                dto.setDeviceId(id);
                dto.setPolicyId(device.getPolicyId()); // 可能为 null，表示解绑策略

                rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_POLICY_MONITOR_REFRESH, id);
                rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_LOG_COMMON_REFRESH_POLICY, dto);

                log.info("已发送设备策略变更消息: {}", dto);
            } catch (Exception e) {
                log.error("发送设备策略变更消息失败", e);
            }
        }
        
        log.info("用户{}综合控制设备{}完成", currentUserId, id);
    }
}
