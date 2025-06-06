package com.SES.service;

import com.SES.dto.device.*;
import com.SES.result.PageResult;

public interface DeviceService {

    /**
     * 新增设备
     * @param deviceDTO
     */
    void addDevice(DeviceDTO deviceDTO);

    /**
     * 删除设备
     * @param deviceDeleteDTO
     */
    void deleteDevice(DeviceDeleteDTO deviceDeleteDTO);

    /**
     * 分页查询设备
     * @param devicePageQueryDTO
     * @return
     */
    PageResult pageQuery(DevicePageQueryDTO devicePageQueryDTO);

    /**
     * 修改设备名称
     * @param id
     * @param deviceNameEditDTO
     */
    void editDeviceName(Long id, DeviceNameEditDTO deviceNameEditDTO);

    /**
     * 修改设备策略
     * @param id
     * @param devicePolicyEditDTO
     */
    void editDevicePolicy(Long id, DevicePolicyEditDTO devicePolicyEditDTO);

    /**
     * 控制设备运行状态
     * @param id
     * @param deviceStatusEditDTO
     */
    void editDeviceStatus(Long id, DeviceStatusEditDTO deviceStatusEditDTO);

    /**
     * 控制设备模式
     * @param id
     * @param deviceModeEditDTO
     */
    void editDeviceMode(Long id, DeviceModeEditDTO deviceModeEditDTO);

    /**
     * 综合控制设备
     * @param id
     * @param deviceControlDTO
     */
    void editDevice(Long id, DeviceControlDTO deviceControlDTO);
}
