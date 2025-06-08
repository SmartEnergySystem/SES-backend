package com.SES.service;

import com.SES.dto.batchItem.BatchItemDTO;
import com.SES.dto.batchItem.BatchItemEditDTO;
import com.SES.entity.BatchItem;

import java.util.List;

public interface BatchItemService {

    /**
     * 新增批量操作条目
     * @param batchItemDTO
     */
    void addBatchItem(BatchItemDTO batchItemDTO);

    /**
     * 删除批量操作条目
     * @param id
     */
    void deleteBatchItem(Long id);

    /**
     * 根据批量操作id查询批量操作条目
     * @param batchId
     * @return
     */
    List<BatchItem> getBatchItemsByBatchId(Long batchId);

    /**
     * 修改批量操作条目内容
     * @param id
     * @param batchItemEditDTO
     */
    void editBatchItem(Long id, BatchItemEditDTO batchItemEditDTO);

    /**
     * 根据批量操作id删除批量操作条目
     * @param batchId
     */
    void deleteBatchItemsByBatchId(Long batchId);
}