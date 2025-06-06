package com.SES.controller.user;

import com.SES.dto.policy.PolicyDTO;
import com.SES.dto.policy.PolicyNameEditDTO;
import com.SES.entity.Policy;
import com.SES.result.Result;
import com.SES.service.PolicyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 策略管理
 */
@RestController
@RequestMapping("/api/policy")
@Slf4j
@Api(tags = "策略相关接口")
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    /**
     * 新增策略
     * @param policyDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增策略")
    public Result<String> addPolicy(@RequestBody PolicyDTO policyDTO) {
        log.info("新增策略：{}", policyDTO);
        try {
            policyService.addPolicy(policyDTO);
            log.info("策略新增成功");
            return Result.success();
        } catch (Exception e) {
            log.error("新增策略失败", e);
            return Result.error("新增策略失败：" + e.getMessage());
        }
    }

    /**
     * 删除策略
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除策略")
    public Result<String> deletePolicy(@PathVariable Long id) {
        log.info("删除策略：{}", id);
        try {
            policyService.deletePolicy(id);
            log.info("策略删除成功");
            return Result.success();
        } catch (Exception e) {
            log.error("删除策略失败", e);
            return Result.error("删除策略失败：" + e.getMessage());
        }
    }

    /**
     * 根据设备id查询策略
     * @param deviceId
     * @return
     */
    @GetMapping("/device/{deviceId}")
    @ApiOperation(value = "根据设备id查询策略")
    public Result<List<Policy>> getPoliciesByDeviceId(@PathVariable Long deviceId) {
        log.info("根据设备id查询策略：{}", deviceId);
        try {
            List<Policy> policies = policyService.getPoliciesByDeviceId(deviceId);
            return Result.success(policies);
        } catch (Exception e) {
            log.error("查询策略失败", e);
            return Result.error("查询策略失败：" + e.getMessage());
        }
    }

    /**
     * 修改策略名称
     * @param id
     * @param policyNameEditDTO
     * @return
     */
    @PutMapping("/{id}/name")
    @ApiOperation(value = "修改策略名称")
    public Result<String> editPolicyName(@PathVariable Long id, 
                                        @RequestBody PolicyNameEditDTO policyNameEditDTO) {
        log.info("修改策略{}名称：{}", id, policyNameEditDTO);
        try {
            policyService.editPolicyName(id, policyNameEditDTO);
            log.info("策略名称修改成功");
            return Result.success();
        } catch (Exception e) {
            log.error("修改策略名称失败", e);
            return Result.error("修改策略名称失败：" + e.getMessage());
        }
    }

    /**
     * 根据设备id删除策略
     * @param deviceId
     * @return
     */
    @DeleteMapping("/device/{deviceId}")
    @ApiOperation(value = "根据设备id删除策略")
    public Result<String> deletePoliciesByDeviceId(@PathVariable Long deviceId) {
        log.info("根据设备id删除策略：{}", deviceId);
        try {
            policyService.deletePoliciesByDeviceId(deviceId);
            log.info("设备策略删除成功");
            return Result.success();
        } catch (Exception e) {
            log.error("删除设备策略失败", e);
            return Result.error("删除设备策略失败：" + e.getMessage());
        }
    }
}
