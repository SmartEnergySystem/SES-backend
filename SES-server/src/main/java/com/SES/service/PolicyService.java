package com.SES.service;

import com.SES.dto.policy.PolicyDTO;
import com.SES.dto.policy.PolicyNameEditDTO;
import com.SES.entity.Policy;
import com.SES.vo.PolicyVO;

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
}
