package com.SES.service.impl;

import com.SES.context.BaseContext;
import com.SES.dto.batch.BatchNameEditDTO;
import com.SES.dto.batch.BatchPageQueryDTO;
import com.SES.exception.BaseException;
import com.SES.mapper.BatchMapper; // 这里修正为正确的 BatchMapper 引入
import com.SES.result.PageResult;
import com.SES.dto.batch.BatchAddDTO; // 补充缺失的引入
import com.SES.entity.Batch; // 补充缺失的引入
import com.SES.service.BatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class BatchServiceImpl implements BatchService {

    @Autowired
    private BatchMapper batchMapper;

    @Override
    public void addBatch(BatchAddDTO batchAddDTO) {
        Batch batch = new Batch();
        BeanUtils.copyProperties(batchAddDTO, batch);

        // 从当前线程中获取登录用户ID
        Long userId = BaseContext.getCurrentId();
        batch.setUserId(userId);

        batch.setCreatetime(LocalDateTime.now());
        batch.setUpdatetime(LocalDateTime.now());

        batchMapper.insert(batch);
    }

    @Override
    public void deleteBatch(Long id) {
        batchMapper.deleteById(id);
    }

    @Override
    public PageResult pageQuery(BatchPageQueryDTO queryDTO) {
        // 从当前线程中获取登录用户ID
        Long userId = BaseContext.getCurrentId();
        queryDTO.setUserId(userId);

        // 计算分页偏移量
        int offset = (queryDTO.getPage() - 1) * queryDTO.getPageSize();
        queryDTO.setOffset(offset);

        List<Batch> batches = batchMapper.pageQuery(queryDTO);
        long total = batches.size(); // 实际项目中应该单独查询总数

        return new PageResult(total, batches);
    }

    @Override
    public void editBatchName(Long id, BatchNameEditDTO nameEditDTO) {
        Batch batch = batchMapper.getById(id);

        if (batch == null) {
            throw new BaseException("用户未登录或token无效");
        }
        // 验证操作权限
        // 验证设备是否属于当前用户
        Long currentUserId = BaseContext.getCurrentId();
        if (!batch.getUserId().equals(currentUserId)) {
            throw new BaseException("设备不存在或无权限操作");
        }

        batch.setName(nameEditDTO.getName());
        batch.setUpdatetime(LocalDateTime.now());

        batchMapper.updateName(id, batch.getName(), batch.getUpdatetime());
    }
}