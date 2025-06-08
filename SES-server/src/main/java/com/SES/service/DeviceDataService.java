package com.SES.service;

import com.SES.dto.deviceData.DeviceDataQueryDTO;
import com.SES.vo.deviceData.DeviceDataVO;

import java.util.List;

public interface DeviceDataService {

    /**
     * 获取设备当前状态
     * @param deviceDataQueryDTO
     * @return
     */
    List<DeviceDataVO> getDataByDeviceIdList(DeviceDataQueryDTO deviceDataQueryDTO);
}
