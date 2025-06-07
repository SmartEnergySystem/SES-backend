package com.SES.service.impl;

import com.SES.context.BaseContext;
import com.SES.dto.BatchItem.BatchItemDTO;
import com.SES.dto.BatchItem.BatchItemEditDTO;
import com.SES.entity.BatchItem;
import com.SES.exception.BaseException;
import com.SES.mapper.BatchItemMapper;
import com.SES.service.BatchItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BatchItemServiceImpl implements BatchItemService {

    @Autowired
    private BatchItemMapper batchItemMapper;

    /**
     * 新增批量操作条目
     * @param batchItemDTO
     */
    @Override
    public void addBatchItem(BatchItemDTO batchItemDTO) {
        Long currentUserId = BaseContext.getCurrentId();
        log.info("当前用户ID：{}", currentUserId);

        if (currentUserId == null) {
            throw new BaseException("用户未登录或token无效");
        }

        BatchItem batchItem = new BatchItem();
        batchItem.setBatchId(batchItemDTO.getBatchId());
        batchItem.setDeviceId(batchItemDTO.getDeviceId());
        batchItem.setIsApplyPolicy(batchItemDTO.getIsApplyPolicy());
        batchItem.setStatus(batchItemDTO.getStatus());
        batchItem.setModeId(batchItemDTO.getModeId());
        batchItem.setPolicyId(batchItemDTO.getPolicyId());

        log.info("准备插入批量操作条目：{}", batchItem);

        batchItemMapper.insert(batchItem);
        log.info("用户{}新增批量操作条目成功", currentUserId);
    }

    /**
     * 删除批量操作条目
     * @param id
     */
    @Override
    public void deleteBatchItem(Long id) {
        Long currentUserId = BaseContext.getCurrentId();

        // 验证批量操作条目是否存在
        BatchItem batchItem = batchItemMapper.getById(id);
        if (batchItem == null) {
            throw new BaseException("批量操作条目不存在");
        }

        batchItemMapper.deleteById(id);
        log.info("用户{}删除批量操作条目：{}", currentUserId, id);
    }

    /**
     * 根据批量操作id查询批量操作条目
     * @param batchId
     * @return
     */
    @Override
    public List<BatchItem> getBatchItemsByBatchId(Long batchId) {
        Long currentUserId = BaseContext.getCurrentId();

        return batchItemMapper.getByBatchId(batchId);
    }

    /**
     * 修改批量操作条目内容
     * @param id
     * @param batchItemEditDTO
     */
    @Override
    public void editBatchItem(Long id, BatchItemEditDTO batchItemEditDTO) {
        Long currentUserId = BaseContext.getCurrentId();

        // 验证批量操作条目是否存在
        BatchItem batchItem = batchItemMapper.getById(id);
        if (batchItem == null) {
            throw new BaseException("批量操作条目不存在");
        }

        // 更新批量操作条目信息
        if (batchItemEditDTO.getDeviceId() != null) {
            batchItem.setDeviceId(batchItemEditDTO.getDeviceId());
        }
        if (batchItemEditDTO.getIsApplyPolicy() != null) {
            batchItem.setIsApplyPolicy(batchItemEditDTO.getIsApplyPolicy());
        }
        if (batchItemEditDTO.getStatus() != null) {
            batchItem.setStatus(batchItemEditDTO.getStatus());
        }
        if (batchItemEditDTO.getModeId() != null) {
            batchItem.setModeId(batchItemEditDTO.getModeId());
        }
        if (batchItemEditDTO.getPolicyId() != null) {
            batchItem.setPolicyId(batchItemEditDTO.getPolicyId());
        }

        batchItemMapper.update(batchItem);

        log.info("用户{}修改批量操作条目：{}", currentUserId, id);
    }

    /**
     * 根据批量操作id删除批量操作条目
     * @param batchId
     */
    @Override
    public void deleteBatchItemsByBatchId(Long batchId) {
        Long currentUserId = BaseContext.getCurrentId();

        batchItemMapper.deleteByBatchId(batchId);
        log.info("用户{}删除批量操作{}的所有条目", currentUserId, batchId);
    }
}