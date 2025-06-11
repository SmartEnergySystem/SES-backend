package com.SES.mapper;

import com.SES.dto.policyItem.PolicyItemTimeRangeDTO;
import com.SES.entity.PolicyItem;
import org.apache.ibatis.annotations.*;

import java.time.LocalTime;
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

    /**
     * 根据 policy_id 查询所有 start_time 和 end_time 输出为时间对
     */
    @Select("SELECT start_time, end_time FROM policy_item WHERE policy_id = #{policyId}")
    List<PolicyItemTimeRangeDTO> getTimePointsByPolicyId(Long policyId);


    /**
     * 根据 policy_id 和 start_time 查询是否存在匹配的策略条目
     */
    @Select("SELECT id, mode_id FROM policy_item WHERE policy_id = #{policyId} AND start_time = #{timePoint}")
    PolicyItem getByPolicyIdAndStartTime(@Param("policyId") Long policyId, @Param("timePoint") LocalTime timePoint);
}
