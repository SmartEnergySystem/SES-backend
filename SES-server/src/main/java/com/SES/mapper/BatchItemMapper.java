package com.SES.mapper;

import com.SES.entity.BatchItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BatchItemMapper {

    /**
     * 插入批量操作条目
     * @param batchItem
     */
    @Insert("INSERT INTO batch_item (batch_id, device_id, isapplypolicy, status, mode_id, policy_id) " +
            "VALUES (#{batchId}, #{deviceId}, #{isApplyPolicy}, #{status}, #{modeId}, #{policyId})")
    void insert(BatchItem batchItem);

    /**
     * 根据批量操作条目ID查询批量操作条目
     * @param id
     * @return
     */
    @Select("SELECT * FROM batch_item WHERE id = #{id}")
    BatchItem getById(Long id);

    /**
     * 根据批量操作ID查询批量操作条目列表
     * @param batchId
     * @return
     */
    @Select("SELECT * FROM batch_item WHERE batch_id = #{batchId}")
    List<BatchItem> getByBatchId(Long batchId);

    /**
     * 删除批量操作条目
     * @param id
     */
    @Delete("DELETE FROM batch_item WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * 根据批量操作ID删除所有批量操作条目
     * @param batchId
     */
    @Delete("DELETE FROM batch_item WHERE batch_id = #{batchId}")
    void deleteByBatchId(Long batchId);

    /**
     * 动态更新批量操作条目
     * @param batchItem
     */
    void update(BatchItem batchItem);
}