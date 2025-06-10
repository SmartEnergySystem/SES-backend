package com.SES.service;

import com.SES.dto.deviceData.DeviceDataQueryDTO;
import com.SES.dto.deviceData.DeviceReportQueryDTO;
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
     * @param deviceReportQueryDTO
     * @return
     */
    DeviceReportResultDTO getDeviceReportByDeviceId(Long id, DeviceReportQueryDTO deviceReportQueryDTO);
}
