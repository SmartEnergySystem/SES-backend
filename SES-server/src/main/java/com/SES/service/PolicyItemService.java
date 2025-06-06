package com.SES.service;

import com.SES.dto.policyItem.PolicyItemDTO;
import com.SES.dto.policyItem.PolicyItemEditDTO;
import com.SES.entity.PolicyItem;

import java.util.List;

public interface PolicyItemService {

    /**
     * 新增策略条目
     * @param policyItemDTO
     */
    void addPolicyItem(PolicyItemDTO policyItemDTO);

    /**
     * 删除策略条目
     * @param id
     */
    void deletePolicyItem(Long id);

    /**
     * 根据策略id查询策略条目
     * @param policyId
     * @return
     */
    List<PolicyItem> getPolicyItemsByPolicyId(Long policyId);

    /**
     * 修改策略条目内容
     * @param id
     * @param policyItemEditDTO
     */
    void editPolicyItem(Long id, PolicyItemEditDTO policyItemEditDTO);

    /**
     * 根据策略id删除策略条目
     * @param policyId
     */
    void deletePolicyItemsByPolicyId(Long policyId);
}
