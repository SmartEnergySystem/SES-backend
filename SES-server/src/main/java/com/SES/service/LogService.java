package com.SES.service;

import com.SES.dto.log.DeviceLogDataDTO;
import com.SES.dto.log.SaveAlertLogDTO;
import com.SES.dto.log.SaveDeviceLogDTO;

public interface LogService {

    /**
     * 储存设备日志
     * @param saveDeviceLogDTO
     */
    void saveDeviceLog(SaveDeviceLogDTO saveDeviceLogDTO);

    /**
     * 获得最新一条设备日志的设备数据部分
     * @param deviceId
     * @return
     */
    DeviceLogDataDTO getLatestDataByDeviceId(Long deviceId);


    /**
     * 储存警报日志
     * @param saveAlertLogDTO
     */
    void saveAlertLog(SaveAlertLogDTO saveAlertLogDTO);


}
