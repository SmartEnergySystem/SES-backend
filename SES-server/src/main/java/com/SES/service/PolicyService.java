package com.SES.service;

import com.SES.dto.policy.PolicyDTO;
import com.SES.dto.policy.PolicyNameEditDTO;
import com.SES.dto.policyItem.PolicyTaskResultDTO;
import com.SES.vo.policy.PolicyVO;

import java.time.LocalTime;
import java.util.List;

public interface PolicyService {

    /**
     * 新增策略
     * @param policyDTO
     */
    void addPolicy(PolicyDTO policyDTO);

    /**
     * 删除策略
     * @param id
     */
    void deletePolicy(Long id);

    /**
     * 根据设备id查询策略
     * @param deviceId
     * @return
     */
    List<PolicyVO> getPoliciesByDeviceId(Long deviceId);

    /**
     * 修改策略名称
     * @param id
     * @param policyNameEditDTO
     */
    void editPolicyName(Long id, PolicyNameEditDTO policyNameEditDTO);

    /**
     * 根据设备id删除策略
     * @param deviceId
     */
    void deletePoliciesByDeviceId(Long deviceId);

    /**
     * 根据id获得策略的Json，包括策略条目
     * @param id
     */
    String getJsonString(Long id);

    /**
     * 获取指定策略的所有时间点（start 和 end）
     * @param policyId 策略ID
     * @return 包含所有时间点的列表
     */
    List<LocalTime> getAllTimePointsByPolicyId(Long policyId);

    /**
     * 根据策略id和时间查询策略控制任务
     * @param policyId
     * @param timePoint
     * @return
     */
    PolicyTaskResultDTO getPolicyTaskByPolicyIdAndStartTime(Long policyId, LocalTime timePoint);
}
