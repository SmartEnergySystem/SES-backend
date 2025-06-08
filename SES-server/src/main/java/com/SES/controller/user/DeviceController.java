package com.SES.controller.user;

import com.SES.dto.device.*;
import com.SES.result.PageResult;
import com.SES.result.Result;
import com.SES.service.DeviceService;
import com.SES.vo.device.DeviceModeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备管理
 */
@RestController
@RequestMapping("/api/device")
@Slf4j
@Api(tags = "设备相关接口")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    /**
     * 新增设备
     * @param deviceDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增设备")
    public Result<String> addDevice(@RequestBody DeviceDTO deviceDTO) {
        log.info("新增设备：{}", deviceDTO);
        try {
            deviceService.addDevice(deviceDTO);
            log.info("设备新增成功");
            return Result.success();
        } catch (Exception e) {
            log.error("新增设备失败", e);
            return Result.error("新增设备失败：" + e.getMessage());
        }
    }

    /**
     * 删除设备
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除设备")
    public Result<String> deleteDevice(@PathVariable Long id) {
        log.info("删除设备：{}", id);
        deviceService.deleteDevice(id);
        return Result.success();
    }

    /**
     * 分页查询设备
     * @param devicePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询设备")
    public Result<PageResult> pageQuery(DevicePageQueryDTO devicePageQueryDTO) {
        log.info("分页查询设备：{}", devicePageQueryDTO);
        PageResult pageResult = deviceService.pageQuery(devicePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询设备模式
     * @param id
     * @return
     */
    @GetMapping("/{id}/mode")
    @ApiOperation(value = "根据id查询设备模式")
    public Result<List<DeviceModeVO>> getModeByDeviceId(@PathVariable Long id) {
        log.info("根据id查询设备模式：{}", id);
       List<DeviceModeVO> result= deviceService.getModeByDeviceId(id);
        return Result.success(result);
    }

    /**
     * 修改设备名称
     * @param id
     * @param deviceNameEditDTO
     * @return
     */
    @PutMapping("/{id}/name")
    @ApiOperation(value = "修改设备名称")
    public Result<String> editDeviceName(@PathVariable Long id, 
                                        @RequestBody DeviceNameEditDTO deviceNameEditDTO) {
        log.info("修改设备{}名称：{}", id, deviceNameEditDTO);
        deviceService.editDeviceName(id, deviceNameEditDTO);
        return Result.success();
    }

    /**
     * 修改设备策略
     * @param id
     * @param devicePolicyEditDTO
     * @return
     */
    @PostMapping("/{id}/policy")
    @ApiOperation(value = "修改设备策略")
    public Result<String> editDevicePolicy(@PathVariable Long id, 
                                          @RequestBody DevicePolicyEditDTO devicePolicyEditDTO) {
        log.info("修改设备{}策略：{}", id, devicePolicyEditDTO);
        deviceService.editDevicePolicy(id, devicePolicyEditDTO);
        return Result.success();
    }

    /**
     * 控制设备运行状态
     * @param id
     * @param deviceStatusEditDTO
     * @return
     */
    @PostMapping("/{id}/status")
    @ApiOperation(value = "控制设备运行状态")
    public Result<String> editDeviceStatus(@PathVariable Long id, 
                                          @RequestBody DeviceStatusEditDTO deviceStatusEditDTO) {
        log.info("控制设备{}状态：{}", id, deviceStatusEditDTO);
        deviceService.editDeviceStatus(id, deviceStatusEditDTO);
        return Result.success();
    }

    /**
     * 控制设备模式
     * @param id
     * @param deviceModeEditDTO
     * @return
     */
    @PostMapping("/{id}/mode")
    @ApiOperation(value = "控制设备模式")
    public Result<String> editDeviceMode(@PathVariable Long id, 
                                        @RequestBody DeviceModeEditDTO deviceModeEditDTO) {
        log.info("控制设备{}模式：{}", id, deviceModeEditDTO);
        deviceService.editDeviceMode(id, deviceModeEditDTO);
        return Result.success();
    }

    /**
     * 综合控制设备
     * @param id
     * @param deviceControlDTO
     * @return
     */
    @PostMapping("/{id}")
    @ApiOperation(value = "综合控制设备")
    public Result<String> editDevice(@PathVariable Long id, 
                                    @RequestBody DeviceControlDTO deviceControlDTO) {
        log.info("综合控制设备{}：{}", id, deviceControlDTO);
        deviceService.editDevice(id, deviceControlDTO);
        return Result.success();
    }
}
