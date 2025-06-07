package com.SES.controller.admin;

import com.SES.dto.Batch.BatchAddDTO;
import com.SES.dto.Batch.BatchNameEditDTO;
import com.SES.dto.Batch.BatchPageQueryDTO;
import com.SES.result.PageResult;
import com.SES.result.Result;
import com.SES.service.BatchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/batch")
@Api(tags = "批量操作接口")
@Slf4j
public class BatchController {

    @Autowired
    private BatchService batchService;

    @PostMapping
    @ApiOperation("新增批量操作")
    public Result<String> addBatch(@RequestBody BatchAddDTO batchAddDTO) {
        log.info("新增批量操作: {}", batchAddDTO);
        batchService.addBatch(batchAddDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除批量操作")
    public Result<String> deleteBatch(@PathVariable Long id) {
        log.info("删除批量操作, ID: {}", id);
        batchService.deleteBatch(id);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分页查询批量操作")
    public Result<PageResult> pageQuery(BatchPageQueryDTO queryDTO) {
        log.info("分页查询批量操作: {}", queryDTO);
        PageResult pageResult = batchService.pageQuery(queryDTO);
        return Result.success(pageResult);
    }

    @PutMapping("/{id}/name")
    @ApiOperation("修改批量操作名称")
    public Result<String> editBatchName(@PathVariable Long id,
                                        @RequestBody BatchNameEditDTO nameEditDTO) {
        log.info("修改批量操作名称, ID: {}, 新名称: {}", id, nameEditDTO.getName());
        batchService.editBatchName(id, nameEditDTO);
        return Result.success();
    }
}