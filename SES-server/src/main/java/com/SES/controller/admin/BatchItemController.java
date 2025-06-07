package com.SES.controller.admin;

import com.SES.dto.BatchItem.BatchItemDTO;
import com.SES.dto.BatchItem.BatchItemEditDTO;
import com.SES.entity.BatchItem;
import com.SES.result.Result;
import com.SES.service.BatchItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 批量操作条目管理
 */
@RestController
@RequestMapping("/api/batchItem")
@Slf4j
@Api(tags = "批量操作条目相关接口")
public class BatchItemController {

    @Autowired
    private BatchItemService batchItemService;

    /**
     * 新增批量操作条目
     * @param batchItemDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增批量操作条目")
    public Result<String> addBatchItem(@RequestBody BatchItemDTO batchItemDTO) {
        log.info("新增批量操作条目：{}", batchItemDTO);
        try {
            batchItemService.addBatchItem(batchItemDTO);
            log.info("批量操作条目新增成功");
            return Result.success();
        } catch (Exception e) {
            log.error("新增批量操作条目失败", e);
            return Result.error("新增批量操作条目失败：" + e.getMessage());
        }
    }

    /**
     * 删除批量操作条目
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除批量操作条目")
    public Result<String> deleteBatchItem(@PathVariable Long id) {
        log.info("删除批量操作条目：{}", id);
        try {
            batchItemService.deleteBatchItem(id);
            log.info("批量操作条目删除成功");
            return Result.success();
        } catch (Exception e) {
            log.error("删除批量操作条目失败", e);
            return Result.error("删除批量操作条目失败：" + e.getMessage());
        }
    }

    /**
     * 根据批量操作id查询批量操作条目
     * @param batchId
     * @return
     */
    @GetMapping("/batch/{batchId}")
    @ApiOperation(value = "根据批量操作id查询批量操作条目")
    public Result<List<BatchItem>> getBatchItemsByBatchId(@PathVariable Long batchId) {
        log.info("根据批量操作id查询批量操作条目：{}", batchId);
        try {
            List<BatchItem> batchItems = batchItemService.getBatchItemsByBatchId(batchId);
            return Result.success(batchItems);
        } catch (Exception e) {
            log.error("查询批量操作条目失败", e);
            return Result.error("查询批量操作条目失败：" + e.getMessage());
        }
    }

    /**
     * 修改批量操作条目内容
     * @param id
     * @param batchItemEditDTO
     * @return
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "修改批量操作条目内容")
    public Result<String> editBatchItem(@PathVariable Long id,
                                        @RequestBody BatchItemEditDTO batchItemEditDTO) {
        log.info("修改批量操作条目{}：{}", id, batchItemEditDTO);
        try {
            batchItemService.editBatchItem(id, batchItemEditDTO);
            log.info("批量操作条目修改成功");
            return Result.success();
        } catch (Exception e) {
            log.error("修改批量操作条目失败", e);
            return Result.error("修改批量操作条目失败：" + e.getMessage());
        }
    }

    /**
     * 根据批量操作id删除批量操作条目
     * @param batchId
     * @return
     */
    @DeleteMapping("/batch/{batchId}")
    @ApiOperation(value = "根据批量操作id删除批量操作条目")
    public Result<String> deleteBatchItemsByBatchId(@PathVariable Long batchId) {
        log.info("根据批量操作id删除批量操作条目：{}", batchId);
        try {
            batchItemService.deleteBatchItemsByBatchId(batchId);
            log.info("批量操作条目删除成功");
            return Result.success();
        } catch (Exception e) {
            log.error("删除批量操作条目失败", e);
            return Result.error("删除批量操作条目失败：" + e.getMessage());
        }
    }
}