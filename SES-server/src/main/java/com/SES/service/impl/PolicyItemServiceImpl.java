package com.SES.service.impl;

import com.SES.context.BaseContext;
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

import java.util.List;

@Service
@Slf4j
public class PolicyItemServiceImpl implements PolicyItemService {

    @Autowired
    private PolicyItemMapper policyItemMapper;

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private DeviceMapper deviceMapper;

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

        //TODO：验证：不同条目的时间范围不能重叠

        PolicyItem policyItem = new PolicyItem();
        policyItem.setPolicyId(policyItemDTO.getPolicyId());
        policyItem.setStartTime(policyItemDTO.getStartTime());
        policyItem.setEndTime(policyItemDTO.getEndTime());
        policyItem.setModeId(policyItemDTO.getModeId());

        log.info("准备插入策略条目：{}", policyItem);
        
        policyItemMapper.insert(policyItem);
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

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(policy.getDeviceId(), currentUserId);
        if (device == null) {
            throw new BaseException("无权限操作此策略");
        }

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

        //TODO：验证：不同条目的时间范围不能重叠

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
        log.info("用户{}删除策略{}的所有条目", currentUserId, policyId);
    }
}
