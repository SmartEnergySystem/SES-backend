package com.SES.service;

import com.SES.dto.log.SaveDeviceLogDTO;

public interface LogService {

    /**
     * 储存设备日志
     * @param saveDeviceLogDTO
     */
    void saveDeviceLog(SaveDeviceLogDTO saveDeviceLogDTO);
}
