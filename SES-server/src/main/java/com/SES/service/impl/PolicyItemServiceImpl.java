package com.SES.service.impl;

import com.SES.config.RabbitMQConfig;
import com.SES.context.BaseContext;
import com.SES.dto.logCommonCache.RefreshLogCommonCachePolicyMessageDTO;
import com.SES.dto.policyItem.PolicyItemDTO;
import com.SES.dto.policyItem.PolicyItemEditDTO;
import com.SES.entity.Device;
import com.SES.entity.Policy;
import com.SES.entity.PolicyItem;
import com.SES.exception.BaseException;
import com.SES.mapper.DeviceMapper;
import com.SES.mapper.PolicyItemMapper;
import com.SES.mapper.PolicyMapper;
import com.SES.service.PolicyItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class PolicyItemServiceImpl implements PolicyItemService {

    @Autowired
    private PolicyItemMapper policyItemMapper;

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 新增策略条目
     * @param policyItemDTO
     */
    @Override
    public void addPolicyItem(PolicyItemDTO policyItemDTO) {
        Long currentUserId = BaseContext.getCurrentId();
        log.info("当前用户ID：{}", currentUserId);
        
        if (currentUserId == null) {
            throw new BaseException("用户未登录或token无效");
        }

        // 验证策略是否存在
        Policy policy = policyMapper.getById(policyItemDTO.getPolicyId());
        if (policy == null) {
            throw new BaseException("策略不存在");
        }

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(policy.getDeviceId(), currentUserId);
        if (device == null) {
            throw new BaseException("无权限操作此策略");
        }

        // 验证：不同条目的时间范围不能重叠
        validateTimeRangeOverlap(policyItemDTO.getPolicyId(), null,
                                policyItemDTO.getStartTime(), policyItemDTO.getEndTime());

        PolicyItem policyItem = new PolicyItem();
        policyItem.setPolicyId(policyItemDTO.getPolicyId());
        policyItem.setStartTime(policyItemDTO.getStartTime());
        policyItem.setEndTime(policyItemDTO.getEndTime());
        policyItem.setModeId(policyItemDTO.getModeId());


        policyItemMapper.insert(policyItem);

        // 如果该策略正在被应用，发送变更消息
        if (Objects.equals(device.getPolicyId(), policy.getId())) {
            sendMessage(policy.getId(), device.getId());
        }

        log.info("用户{}新增策略条目成功", currentUserId);
    }

    /**
     * 删除策略条目
     * @param id
     */
    @Override
    public void deletePolicyItem(Long id) {
        Long currentUserId = BaseContext.getCurrentId();

        // 验证策略条目是否存在
        PolicyItem policyItem = policyItemMapper.getById(id);
        if (policyItem == null) {
            throw new BaseException("策略条目不存在");
        }

        // 验证策略是否存在
        Policy policy = policyMapper.getById(policyItem.getPolicyId());
        if (policy == null) {
            throw new BaseException("策略不存在");
        }

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(policy.getDeviceId(), currentUserId);
        if (device == null) {
            throw new BaseException("无权限操作此策略条目");
        }

        policyItemMapper.deleteById(id);

        // 如果该策略正在被应用，发送变更消息
        if (Objects.equals(device.getPolicyId(), policy.getId())) {
            sendMessage(policy.getId(), device.getId());
        }

        log.info("用户{}删除策略条目：{}", currentUserId, id);
    }

    /**
     * 根据策略id查询策略条目
     * @param policyId
     * @return
     */
    @Override
    public List<PolicyItem> getPolicyItemsByPolicyId(Long policyId) {
        Long currentUserId = BaseContext.getCurrentId();

        // 验证策略是否存在
        Policy policy = policyMapper.getById(policyId);
        if (policy == null) {
            throw new BaseException("策略不存在");
        }

        // TODO:有的内部服务类会调用这些服务（例如get方法），导致无权限。应该把用户相关验证变成单独函数，在controller提前调用
//        // 验证设备是否属于当前用户
//        Device device = deviceMapper.getByIdAndUserId(policy.getDeviceId(), currentUserId);
//        if (device == null) {
//            throw new BaseException("无权限操作此策略");
//        }

        return policyItemMapper.getByPolicyId(policyId);
    }

    /**
     * 修改策略条目内容
     * @param id
     * @param policyItemEditDTO
     */
    @Override
    public void editPolicyItem(Long id, PolicyItemEditDTO policyItemEditDTO) {
        Long currentUserId = BaseContext.getCurrentId();

        // 验证策略条目是否存在
        PolicyItem policyItem = policyItemMapper.getById(id);
        if (policyItem == null) {
            throw new BaseException("策略条目不存在");
        }

        // 验证策略是否存在
        Policy policy = policyMapper.getById(policyItem.getPolicyId());
        if (policy == null) {
            throw new BaseException("策略不存在");
        }

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(policy.getDeviceId(), currentUserId);
        if (device == null) {
            throw new BaseException("无权限操作此策略条目");
        }

        // 验证：不同条目的时间范围不能重叠
        LocalTime newStartTime = policyItemEditDTO.getStartTime() != null ?
                                    policyItemEditDTO.getStartTime() : policyItem.getStartTime();
        LocalTime newEndTime = policyItemEditDTO.getEndTime() != null ?
                                  policyItemEditDTO.getEndTime() : policyItem.getEndTime();

        validateTimeRangeOverlap(policyItem.getPolicyId(), id, newStartTime, newEndTime);

        // 更新策略条目信息
        if (policyItemEditDTO.getStartTime() != null) {
            policyItem.setStartTime(policyItemEditDTO.getStartTime());
        }
        if (policyItemEditDTO.getEndTime() != null) {
            policyItem.setEndTime(policyItemEditDTO.getEndTime());
        }
        if (policyItemEditDTO.getModeId() != null) {
            policyItem.setModeId(policyItemEditDTO.getModeId());
        }

        policyItemMapper.update(policyItem);

        // 如果该策略正在被应用，发送变更消息
        if (Objects.equals(device.getPolicyId(), policy.getId())) {
            sendMessage(policy.getId(), device.getId());
        }

        log.info("用户{}修改策略条目：{}", currentUserId, id);
    }

    /**
     * 根据策略id删除策略条目
     * @param policyId
     */
    @Override
    public void deletePolicyItemsByPolicyId(Long policyId) {
        Long currentUserId = BaseContext.getCurrentId();

        // 验证策略是否存在
        Policy policy = policyMapper.getById(policyId);
        if (policy == null) {
            throw new BaseException("策略不存在");
        }

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(policy.getDeviceId(), currentUserId);
        if (device == null) {
            throw new BaseException("无权限操作此策略");
        }

        policyItemMapper.deleteByPolicyId(policyId);

        // 如果该策略正在被应用，发送变更消息
        if (Objects.equals(device.getPolicyId(), policy.getId())) {
            sendMessage(policy.getId(), device.getId());
        }


        log.info("用户{}删除策略{}的所有条目", currentUserId, policyId);
    }



    /**
     * 验证时间范围是否重叠
     * @param policyId 策略ID
     * @param excludeItemId 排除的策略条目ID（修改时使用，新增时为null）
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    private void validateTimeRangeOverlap(Long policyId, Long excludeItemId,
                                         LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            return; // 如果时间为空，跳过验证
        }

        // 确保开始时间早于结束时间
        if (!startTime.isBefore(endTime)) {
            throw new BaseException("开始时间必须早于结束时间");
        }

        // 获取该策略的所有条目
        List<PolicyItem> existingItems = policyItemMapper.getByPolicyId(policyId);

        for (PolicyItem existingItem : existingItems) {
            // 跳过当前正在修改的条目
            if (existingItem.getId().equals(excludeItemId)) {
                continue;
            }

            // 检查时间范围是否重叠
            if (isTimeRangeOverlap(startTime, endTime,
                                  existingItem.getStartTime(), existingItem.getEndTime())) {
                throw new BaseException("时间范围与现有策略条目重叠，请调整时间设置");
            }
        }
    }

    /**
     * 判断两个时间范围是否重叠
     * @param start1 第一个时间范围的开始时间
     * @param end1 第一个时间范围的结束时间
     * @param start2 第二个时间范围的开始时间
     * @param end2 第二个时间范围的结束时间
     * @return 是否重叠
     */
    private boolean isTimeRangeOverlap(LocalTime start1, LocalTime end1,
                                       LocalTime start2, LocalTime end2) {
        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false; // 如果任何时间为空，认为不重叠
        }

        // 两个时间范围重叠的条件：
        // 1. start1 < end2 且 start2 < end1
        // 这是标准的区间重叠判断算法
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    /**
     * 发送策略内容变更消息
     */
    private void sendMessage(Long policyId, Long deviceId) {
        try {
            RefreshLogCommonCachePolicyMessageDTO dto = new RefreshLogCommonCachePolicyMessageDTO();
            dto.setDeviceId(deviceId);
            dto.setPolicyId(policyId);

            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_POLICY_MONITOR_REFRESH, deviceId);
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_LOG_COMMON_REFRESH_POLICY, dto);

            log.info("已发送设备策略变更消息: {}", dto);
        } catch (Exception e) {
            log.error("发送设备策略变更消息失败", e);
        }
    }
}
