package com.SES.controller.user;

import com.SES.dto.policyItem.PolicyItemDTO;
import com.SES.dto.policyItem.PolicyItemEditDTO;
import com.SES.entity.PolicyItem;
import com.SES.result.Result;
import com.SES.service.PolicyItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 策略条目管理
 */
@RestController
@RequestMapping("/api/policyItem")
@Slf4j
@Api(tags = "策略条目相关接口")
public class PolicyItemController {

    @Autowired
    private PolicyItemService policyItemService;

    /**
     * 新增策略条目
     * @param policyItemDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增策略条目")
    public Result<String> addPolicyItem(@RequestBody PolicyItemDTO policyItemDTO) {
        log.info("新增策略条目：{}", policyItemDTO);
        try {
            policyItemService.addPolicyItem(policyItemDTO);
            log.info("策略条目新增成功");
            return Result.success();
        } catch (Exception e) {
            log.error("新增策略条目失败", e);
            return Result.error("新增策略条目失败：" + e.getMessage());
        }
    }

    /**
     * 删除策略条目
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除策略条目")
    public Result<String> deletePolicyItem(@PathVariable Long id) {
        log.info("删除策略条目：{}", id);
        try {
            policyItemService.deletePolicyItem(id);
            log.info("策略条目删除成功");
            return Result.success();
        } catch (Exception e) {
            log.error("删除策略条目失败", e);
            return Result.error("删除策略条目失败：" + e.getMessage());
        }
    }

    /**
     * 根据策略id查询策略条目
     * @param policyId
     * @return
     */
    @GetMapping("/policy/{policyId}")
    @ApiOperation(value = "根据策略id查询策略条目")
    public Result<List<PolicyItem>> getPolicyItemsByPolicyId(@PathVariable Long policyId) {
        log.info("根据策略id查询策略条目：{}", policyId);
        try {
            List<PolicyItem> policyItems = policyItemService.getPolicyItemsByPolicyId(policyId);
            return Result.success(policyItems);
        } catch (Exception e) {
            log.error("查询策略条目失败", e);
            return Result.error("查询策略条目失败：" + e.getMessage());
        }
    }

    /**
     * 修改策略条目内容
     * @param id
     * @param policyItemEditDTO
     * @return
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "修改策略条目内容")
    public Result<String> editPolicyItem(@PathVariable Long id, 
                                        @RequestBody PolicyItemEditDTO policyItemEditDTO) {
        log.info("修改策略条目{}：{}", id, policyItemEditDTO);
        try {
            policyItemService.editPolicyItem(id, policyItemEditDTO);
            log.info("策略条目修改成功");
            return Result.success();
        } catch (Exception e) {
            log.error("修改策略条目失败", e);
            return Result.error("修改策略条目失败：" + e.getMessage());
        }
    }

    /**
     * 根据策略id删除策略条目
     * @param policyId
     * @return
     */
    @DeleteMapping("/policy/{policyId}")
    @ApiOperation(value = "根据策略id删除策略条目")
    public Result<String> deletePolicyItemsByPolicyId(@PathVariable Long policyId) {
        log.info("根据策略id删除策略条目：{}", policyId);
        try {
            policyItemService.deletePolicyItemsByPolicyId(policyId);
            log.info("策略条目删除成功");
            return Result.success();
        } catch (Exception e) {
            log.error("删除策略条目失败", e);
            return Result.error("删除策略条目失败：" + e.getMessage());
        }
    }
}
