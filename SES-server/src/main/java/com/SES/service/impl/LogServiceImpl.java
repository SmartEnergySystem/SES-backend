package com.SES.service.impl;

import com.SES.dto.log.*;
import com.SES.mapper.AlertLogMapper;
import com.SES.mapper.DeviceLogMapper;
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

    @Autowired
    private AlertLogMapper alertLogMapper;

    /**
     * 储存设备日志
     * @param saveDeviceLogDTO 日志数据
     */
    @Override
    public void saveDeviceLog(SaveDeviceLogDTO saveDeviceLogDTO) {
        Long deviceId = saveDeviceLogDTO.getDeviceId();
        if (deviceId == null) {
            log.warn("设备ID为空，无法保存设备日志");
            return;
        }

        // 从缓存中获取设备公共信息，要求重试和返回兜底数据
        LogCommonDTO commonDTO = logCommonCacheService.getWithSyncRefreshAndFallback(deviceId);


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

        // 计算用电量（Wh）
        long durationMillis = Duration.between(startTime, endTime).toMillis();
        float powerW = saveDeviceLogDTO.getPower(); // 单位：W
        float energyConsumption = (float) ((powerW * durationMillis) / 3_600_000.0);

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

    /**
     * 获得最新一条日志的设备数据部分
     * @param deviceId
     * @return
     */
    public DeviceLogDataDTO getLatestDataByDeviceId(Long deviceId){
        return deviceLogMapper.getLatestDataByDeviceId(deviceId);
    }

    /**
     * 储存警报日志
     * @param saveAlertLogDTO
     */
    @Override
    public void saveAlertLog(SaveAlertLogDTO saveAlertLogDTO) {
        Long deviceId = saveAlertLogDTO.getDeviceId();
        if (deviceId == null) {
            log.warn("设备ID为空，无法保存警报日志");
            return;
        }

        // 从缓存中获取设备公共信息，要求重试和返回兜底数据
        LogCommonDTO commonDTO = logCommonCacheService.getWithSyncRefreshAndFallback(deviceId);

        AlertLogDTO alertLogDTO = new AlertLogDTO();

        // 构建完整日志数据
        alertLogDTO.setUserId(commonDTO.getUserId());
        alertLogDTO.setUsername(commonDTO.getUsername());
        alertLogDTO.setDeviceId(deviceId);
        alertLogDTO.setDeviceName(commonDTO.getDeviceName());

        alertLogDTO.setTime(saveAlertLogDTO.getTime());
        alertLogDTO.setLevel(saveAlertLogDTO.getLevel());
        alertLogDTO.setStatus(saveAlertLogDTO.getStatus());
        alertLogDTO.setModeName(saveAlertLogDTO.getModeName());

        alertLogDTO.setPolicyName(commonDTO.getPolicyName());
        alertLogDTO.setPolicy(commonDTO.getPolicyJson());

        alertLogDTO.setMessage(saveAlertLogDTO.getMessage());

        // 插入数据库
        try {
            alertLogMapper.insert(alertLogDTO);
            log.info("已保存设备 {} 的警报日志，logId: {}，警报信息: {}", deviceId, alertLogDTO.getId(), alertLogDTO.getMessage());
        } catch (Exception e) {
            log.error("保存设备 {} 的警报日志失败", deviceId, e);
        }


    }
}
