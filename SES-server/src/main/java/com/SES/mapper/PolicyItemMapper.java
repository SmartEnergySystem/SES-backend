package com.SES.mapper;

import com.SES.entity.PolicyItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PolicyItemMapper {

    /**
     * 插入策略条目
     * @param policyItem
     */
    @Insert("INSERT INTO policy_item (policy_id, start_time, end_time, mode_id) " +
            "VALUES (#{policyId}, #{startTime}, #{endTime}, #{modeId})")
    void insert(PolicyItem policyItem);

    /**
     * 根据策略条目ID查询策略条目
     * @param id
     * @return
     */
    @Select("SELECT * FROM policy_item WHERE id = #{id}")
    PolicyItem getById(Long id);

    /**
     * 根据策略ID查询策略条目列表
     * @param policyId
     * @return
     */
    @Select("SELECT * FROM policy_item WHERE policy_id = #{policyId} ORDER BY start_time ASC")
    List<PolicyItem> getByPolicyId(Long policyId);

    /**
     * 删除策略条目
     * @param id
     */
    @Delete("DELETE FROM policy_item WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * 根据策略ID删除所有策略条目
     * @param policyId
     */
    @Delete("DELETE FROM policy_item WHERE policy_id = #{policyId}")
    void deleteByPolicyId(Long policyId);

    /**
     * 动态更新策略条目
     * @param policyItem
     */
    void update(PolicyItem policyItem);
}
