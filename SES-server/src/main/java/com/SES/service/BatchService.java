package com.SES.service;

import com.SES.dto.batch.BatchAddDTO;
import com.SES.dto.batch.BatchNameEditDTO;
import com.SES.dto.batch.BatchPageQueryDTO;
import com.SES.result.PageResult;

public interface BatchService {

    /**
     * 新增批量操作
     * @param batchAddDTO
     */
    void addBatch(BatchAddDTO batchAddDTO);

    /**
     * 删除批量操作
     * @param id
     */
    void deleteBatch(Long id);

    /**
     * 分页查询批量操作
     * @param queryDTO
     * @return
     */
    PageResult pageQuery(BatchPageQueryDTO queryDTO);

    /**
     * 修改批量操作名称
     * @param id
     * @param nameEditDTO
     */
    void editBatchName(Long id, BatchNameEditDTO nameEditDTO);
}