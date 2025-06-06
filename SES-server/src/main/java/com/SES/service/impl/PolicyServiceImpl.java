package com.SES.service.impl;

import com.SES.context.BaseContext;
import com.SES.dto.policy.PolicyDTO;
import com.SES.dto.policy.PolicyNameEditDTO;
import com.SES.entity.Device;
import com.SES.entity.Policy;
import com.SES.exception.BaseException;
import com.SES.mapper.DeviceMapper;
import com.SES.mapper.PolicyMapper;
import com.SES.service.PolicyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class PolicyServiceImpl implements PolicyService {

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    /**
     * 新增策略
     * @param policyDTO
     */
    @Override
    public void addPolicy(PolicyDTO policyDTO) {
        Long currentUserId = BaseContext.getCurrentId();
        log.info("当前用户ID：{}", currentUserId);
        
        if (currentUserId == null) {
            throw new BaseException("用户未登录或token无效");
        }

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(policyDTO.getDeviceId(), currentUserId);
        if (device == null) {
            throw new BaseException("设备不存在或无权限操作");
        }

        Policy policy = new Policy();
        policy.setDeviceId(policyDTO.getDeviceId());
        policy.setName(policyDTO.getName());
        policy.setCreatetime(LocalDateTime.now());
        policy.setUpdatetime(LocalDateTime.now());

        log.info("准备插入策略：{}", policy);
        
        policyMapper.insert(policy);
        log.info("用户{}新增策略成功：{}", currentUserId, policyDTO.getName());
    }

    /**
     * 删除策略
     * @param id
     */
    @Override
    public void deletePolicy(Long id) {
        Long currentUserId = BaseContext.getCurrentId();

        // 验证策略是否存在
        Policy policy = policyMapper.getById(id);
        if (policy == null) {
            throw new BaseException("策略不存在");
        }

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(policy.getDeviceId(), currentUserId);
        if (device == null) {
            throw new BaseException("无权限操作此策略");
        }

        policyMapper.deleteById(id);
        // TODO: 补充级联删除
        // 因为使用逻辑外键，应该级联删除策略条目表
        log.info("用户{}删除策略：{}", currentUserId, policy.getName());
    }

    /**
     * 根据设备id查询策略
     * @param deviceId
     * @return
     */
    @Override
    public List<Policy> getPoliciesByDeviceId(Long deviceId) {
        Long currentUserId = BaseContext.getCurrentId();

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(deviceId, currentUserId);
        if (device == null) {
            throw new BaseException("设备不存在或无权限操作");
        }

        return policyMapper.getByDeviceId(deviceId);
    }

    /**
     * 修改策略名称
     * @param id
     * @param policyNameEditDTO
     */
    @Override
    public void editPolicyName(Long id, PolicyNameEditDTO policyNameEditDTO) {
        Long currentUserId = BaseContext.getCurrentId();

        // 验证策略是否存在
        Policy policy = policyMapper.getById(id);
        if (policy == null) {
            throw new BaseException("策略不存在");
        }

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(policy.getDeviceId(), currentUserId);
        if (device == null) {
            throw new BaseException("无权限操作此策略");
        }

        policy.setName(policyNameEditDTO.getName());
        policy.setUpdatetime(LocalDateTime.now());
        policyMapper.update(policy);
        
        log.info("用户{}修改策略{}名称为：{}", currentUserId, id, policyNameEditDTO.getName());
    }

    /**
     * 根据设备id删除策略
     * @param deviceId
     */
    @Override
    public void deletePoliciesByDeviceId(Long deviceId) {
        Long currentUserId = BaseContext.getCurrentId();

        // 验证设备是否属于当前用户
        Device device = deviceMapper.getByIdAndUserId(deviceId, currentUserId);
        if (device == null) {
            throw new BaseException("设备不存在或无权限操作");
        }

        policyMapper.deleteByDeviceId(deviceId);
        // TODO: 补充级联删除
        // 因为使用逻辑外键，应该级联删除策略条目表
        log.info("用户{}删除设备{}的所有策略", currentUserId, deviceId);
    }
}
