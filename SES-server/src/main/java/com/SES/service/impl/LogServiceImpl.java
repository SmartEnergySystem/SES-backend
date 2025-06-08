package com.SES.service.impl;

import com.SES.dto.log.DeviceLogDTO;
import com.SES.dto.log.LogCommonDTO;
import com.SES.dto.log.SaveDeviceLogDTO;
import com.SES.mapper.DeviceLogMapper;
import com.SES.service.DeviceIdCacheService;
import com.SES.service.LogCommonCacheService;
import com.SES.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Slf4j
public class LogServiceImpl implements LogService {

    @Autowired
    private LogCommonCacheService logCommonCacheService;

    @Autowired
    private DeviceLogMapper deviceLogMapper;

    /**
     * 储存设备日志
     * @param saveDeviceLogDTO 日志数据
     */
    @Override
    public void saveDeviceLog(SaveDeviceLogDTO saveDeviceLogDTO) {
        Long deviceId = saveDeviceLogDTO.getDeviceId();
        if (deviceId == null) {
            log.warn("设备ID为空，无法保存日志");
            return;
        }

        // 从缓存中获取设备公共信息
        LogCommonDTO commonDTO = logCommonCacheService.getLogCommonDTO(deviceId);
        if (commonDTO == null) {
            log.warn("未找到设备 {} 的公共信息，无法保存日志", deviceId);
            return;
        }

        LocalDateTime startTime = saveDeviceLogDTO.getStartTime();
        LocalDateTime endTime = saveDeviceLogDTO.getEndTime();

        if (startTime == null || endTime == null) {
            log.warn("开始或结束时间为空，无法计算用电量");
            return;
        }

        if (saveDeviceLogDTO.getPower() == null) {
            log.warn("功率数据为空，无法计算用电量");
            return;
        }

        // 计算用电量（kWh）
        double durationHours = Duration.between(startTime, endTime).toMinutes() / 60.0; // 转换为小时
        double powerKw = saveDeviceLogDTO.getPower() / 1000.0; // W -> kW
        int energyConsumption = (int) Math.round(powerKw * durationHours * 1000); // kWh -> 整数，单位：Wh（保留整数即可）

        // 构建完整日志数据
        DeviceLogDTO deviceLogDTO = new DeviceLogDTO();
        deviceLogDTO.setUserId(commonDTO.getUserId());
        deviceLogDTO.setUsername(commonDTO.getUsername());
        deviceLogDTO.setDeviceId(deviceId);
        deviceLogDTO.setDeviceName(commonDTO.getDeviceName());
        deviceLogDTO.setStartTime(startTime);
        deviceLogDTO.setEndTime(endTime);
        deviceLogDTO.setStatus(saveDeviceLogDTO.getStatus());
        deviceLogDTO.setModeName(saveDeviceLogDTO.getModeName());
        deviceLogDTO.setPolicyName(commonDTO.getPolicyName());
        deviceLogDTO.setPolicy(commonDTO.getPolicyJson());
        deviceLogDTO.setPower(saveDeviceLogDTO.getPower());
        deviceLogDTO.setEnergyConsumption(energyConsumption);

        // 插入数据库
        try {
            deviceLogMapper.insert(deviceLogDTO);
            log.info("已保存设备 {} 的日志，logId: {}，用电量: {} Wh", deviceId, deviceLogDTO.getId(), energyConsumption);
        } catch (Exception e) {
            log.error("保存设备 {} 的日志失败", deviceId, e);
        }
    }
}
