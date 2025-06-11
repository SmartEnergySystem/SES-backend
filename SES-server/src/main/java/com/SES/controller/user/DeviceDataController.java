package com.SES.controller.user;

import com.SES.dto.deviceData.AlertReportResultDTO;
import com.SES.dto.deviceData.DeviceDataQueryDTO;
import com.SES.dto.deviceData.ReportQueryDTO;
import com.SES.dto.deviceData.DeviceReportResultDTO;
import com.SES.result.Result;
import com.SES.service.DeviceDataService;
import com.SES.vo.deviceData.DeviceDataVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device")
@Slf4j
@Api(tags = "设备数据管理相关接口")
public class DeviceDataController {

    @Autowired
    private DeviceDataService deviceDataService;

    /**
     * 获取设备当前状态，可批量获取
     * @param deviceDataQueryDTO
     * @return
     */
    @GetMapping("/data")
    @ApiOperation(value = "获取设备当前状态")
    public Result<List<DeviceDataVO>> getDataByDeviceIdList(DeviceDataQueryDTO deviceDataQueryDTO) {
        List<DeviceDataVO> result = deviceDataService.getDataByDeviceIdList(deviceDataQueryDTO);
        return Result.success(result);
    }

    /**
     * 根据设备id查询设备报表
     * @param id
     * @return
     */
    @PostMapping("{id}/deviceReport")
    @ApiOperation(value = "根据设备id查询设备报表")
    public Result<DeviceReportResultDTO> getDeviceReportByDeviceId(@PathVariable Long id,@RequestBody ReportQueryDTO reportQueryDTO) {
        DeviceReportResultDTO result = deviceDataService.getDeviceReportByDeviceId(id, reportQueryDTO);
        return Result.success(result);
    }

    /**
     * 根据设备id查询设备报表
     * @param id
     * @return
     */
    @PostMapping("{id}/alertReport")
    @ApiOperation(value = "根据设备id查询警报报表")
    public Result<AlertReportResultDTO> getAlertReportByDeviceId(@PathVariable Long id, @RequestBody ReportQueryDTO reportQueryDTO) {
        AlertReportResultDTO result = deviceDataService.getAlertReportByDeviceId(id, reportQueryDTO);
        return Result.success(result);
    }
}
