package com.SES.service.impl;

import com.SES.context.BaseContext;
import com.SES.dto.Batch.BatchNameEditDTO;
import com.SES.dto.Batch.BatchPageQueryDTO;
import com.SES.dto.device.DeviceControlDTO;
import com.SES.entity.BatchItem;
import com.SES.entity.Device;
import com.SES.exception.BaseException;
import com.SES.mapper.BatchItemMapper;
import com.SES.mapper.BatchMapper; // 这里修正为正确的 BatchMapper 引入
import com.SES.result.PageResult;
import com.SES.dto.Batch.BatchAddDTO; // 补充缺失的引入
import com.SES.entity.Batch; // 补充缺失的引入
import com.SES.service.BatchService;
import com.SES.service.DeviceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BatchServiceImpl implements BatchService {
    @Autowired
    private BatchItemMapper batchItemMapper; // 注入 BatchItemMapper

    @Autowired
    private DeviceService deviceService; // 注入 DeviceService
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
    @Override
    public void applyBatch(Long id) {
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            throw new BaseException("用户未登录或token无效");
        }

        // 根据批量操作id查询批量操作条目
        List<BatchItem> batchItems = batchItemMapper.getByBatchId(id);
        if (batchItems == null || batchItems.isEmpty()) {
            throw new BaseException("该批量操作下没有条目");
        }

        // 遍历批量操作条目，调用设备服务进行操作
        for (BatchItem batchItem : batchItems) {
            Long deviceId = batchItem.getDeviceId();
            Integer status = batchItem.getStatus();
            Long modeId = batchItem.getModeId();
            Integer isApplyPolicy = batchItem.getIsApplyPolicy();
            Long policyId = batchItem.getPolicyId();

            // 构建设备控制DTO
            DeviceControlDTO deviceControlDTO = new DeviceControlDTO();
            deviceControlDTO.setIsApplyPolicy(isApplyPolicy);
            deviceControlDTO.setPolicyId(policyId);
            deviceControlDTO.setStatus(status);
            deviceControlDTO.setModeId(modeId);

            // 调用设备服务的综合控制方法
            deviceService.editDevice(deviceId, deviceControlDTO);
        }
    }
}