package com.SES.service;

import com.SES.dto.deviceApi.DeviceInitApiResultDTO;
import com.SES.dto.deviceApi.DeviceQueryApiResultDTO;

public interface DeviceApiService {

    /**
     * 设备初始化api：
     * 为deviceId的设备填写sim_device表和sim_device_mode表
     * @param deviceId
     * @param type
     * @return 返回该设备的默认模式名与模式名列表，用于填写device_mode
     */
    DeviceInitApiResultDTO deviceInitApi(Long deviceId, String type);

    /**
     * 设备控制api：
     * 为deviceId的设备修改sim_device表中状态
     * @param deviceId
     * @param status
     * @param modeName
     */
    void deviceControlApi(Long deviceId, Integer status, String modeName);

    /**
     * 设备查询api：
     * 查询模拟设备的当前状态
     * @param deviceId
     * @return
     */
    DeviceQueryApiResultDTO deviceQueryApi(Long deviceId);
}
