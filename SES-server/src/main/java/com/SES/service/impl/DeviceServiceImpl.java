package com.SES.service.impl;

import com.SES.context.BaseContext;
import com.SES.dto.device.*;
import com.SES.entity.Device;
import com.SES.entity.OperationLog;
import com.SES.entity.User;
import com.SES.exception.BaseException;
import com.SES.mapper.DeviceMapper;
import com.SES.mapper.OperationLogMapper;
import com.SES.mapper.UserMapper;
import com.SES.result.PageResult;
import com.SES.service.DeviceService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Autowired
    private UserMapper userMapper;

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
        device.setLastKnownStatus(0); // 默认状态为关闭

        log.info("准备插入设备：{}", device);

        // TODO: 根据设备类型(deviceDTO.getType())初始化设备的默认模式等信息
        // 这里需要实现内置设备类型的生成函数

        deviceMapper.insert(device);
        log.info("用户{}新增设备成功：{}", currentUserId, deviceDTO.getName());
    }

    /**
     * 删除设备
     * @param deviceDeleteDTO
     */
    @Override
    public void deleteDevice(DeviceDeleteDTO deviceDeleteDTO) {
        Long currentUserId = BaseContext.getCurrentId();
        Long deviceId = deviceDeleteDTO.getId();

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(deviceId, currentUserId);
        if (device == null) {
            throw new BaseException("设备不存在或无权限操作");
        }

        deviceMapper.deleteById(deviceId);
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

        device.setName(deviceNameEditDTO.getName());
        deviceMapper.update(device);
        
        log.info("用户{}修改设备{}名称为：{}", currentUserId, id, deviceNameEditDTO.getName());
    }

    /**
     * 修改设备策略
     * @param id
     * @param devicePolicyEditDTO
     */
    @Override
    public void editDevicePolicy(Long id, DevicePolicyEditDTO devicePolicyEditDTO) {
        Long currentUserId = BaseContext.getCurrentId();

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
            device.setPolicyId(devicePolicyEditDTO.getPolicyId());
        }

        deviceMapper.update(device);
        
        // 记录操作日志
        recordOperationLog(currentUserId, device, devicePolicyEditDTO.getIsApplyPolicy(), 
                          null, null, devicePolicyEditDTO.getPolicyId());
        
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

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(id, currentUserId);
        if (device == null) {
            throw new BaseException("设备不存在或无权限操作");
        }

        device.setLastKnownStatus(deviceStatusEditDTO.getStatus());
        deviceMapper.update(device);
        
        // 记录操作日志
        recordOperationLog(currentUserId, device, null, 
                          deviceStatusEditDTO.getStatus(), null, null);
        
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

        device.setLastKnownModeId(deviceModeEditDTO.getModeId());
        deviceMapper.update(device);
        
        // 记录操作日志
        recordOperationLog(currentUserId, device, null, 
                          null, deviceModeEditDTO.getModeId(), null);
        
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

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(id, currentUserId);
        if (device == null) {
            throw new BaseException("设备不存在或无权限操作");
        }

        // 处理策略
        if (deviceControlDTO.getIsApplyPolicy() != null) {
            if (deviceControlDTO.getIsApplyPolicy() == 0) {
                // 解绑策略
                device.setPolicyId(null);
            } else if (deviceControlDTO.getIsApplyPolicy() == 1) {
                // 应用策略
                device.setPolicyId(deviceControlDTO.getPolicyId());
            }
        }

        // 处理状态
        if (deviceControlDTO.getStatus() != null) {
            device.setLastKnownStatus(deviceControlDTO.getStatus());
        }

        // 处理模式
        if (deviceControlDTO.getModeId() != null) {
            device.setLastKnownModeId(deviceControlDTO.getModeId());
        }

        deviceMapper.update(device);
        
        // 记录操作日志
        recordOperationLog(currentUserId, device, deviceControlDTO.getIsApplyPolicy(), 
                          deviceControlDTO.getStatus(), deviceControlDTO.getModeId(), 
                          deviceControlDTO.getPolicyId());
        
        log.info("用户{}综合控制设备{}", currentUserId, id);
    }

    /**
     * 记录操作日志
     */
    private void recordOperationLog(Long userId, Device device, Integer isApplyPolicy, 
                                   Integer status, Long modeId, Long policyId) {
        try {
            User user = userMapper.getById(userId);
            
            OperationLog operationLog = OperationLog.builder()
                    .userId(userId)
                    .userUsername(user != null ? user.getUsername() : "unknown")
                    .deviceId(device.getId())
                    .deviceName(device.getName())
                    .time(LocalDateTime.now())
                    .isApplyPolicy(isApplyPolicy)
                    .status(status)
                    .modeName(modeId != null ? "mode_" + modeId : null) // TODO: 获取实际模式名称
                    .policyName(policyId != null ? "policy_" + policyId : null) // TODO: 获取实际策略名称
                    .policy(null) // TODO: 获取策略详情JSON
                    .batchName(null)
                    .build();
            
            operationLogMapper.insert(operationLog);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
            // 不抛出异常，避免影响主业务流程
        }
    }
}
