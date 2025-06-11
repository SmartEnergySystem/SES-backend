package com.SES.service;

import com.SES.dto.deviceData.AlertReportResultDTO;
import com.SES.dto.deviceData.DeviceDataQueryDTO;
import com.SES.dto.deviceData.ReportQueryDTO;
import com.SES.dto.deviceData.DeviceReportResultDTO;
import com.SES.vo.deviceData.DeviceDataVO;

import java.util.List;

public interface DeviceDataService {

    /**
     * 获取设备当前状态
     * @param deviceDataQueryDTO
     * @return
     */
    List<DeviceDataVO> getDataByDeviceIdList(DeviceDataQueryDTO deviceDataQueryDTO);

    /**
     * 根据设备id查询设备报表
     *
     * @param id
     * @param reportQueryDTO
     * @return
     */
    DeviceReportResultDTO getDeviceReportByDeviceId(Long id, ReportQueryDTO reportQueryDTO);

    /**
     * 根据设备id查询警报报表
     *
     * @param id
     * @param reportQueryDTO
     * @return
     */
    AlertReportResultDTO getAlertReportByDeviceId(Long id, ReportQueryDTO reportQueryDTO);
}
