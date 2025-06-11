package com.SES.service.impl;

import com.SES.constant.AlertLevelConstant;
import com.SES.constant.DeviceStatusConstant;
import com.SES.dto.deviceApi.DeviceQueryApiResultDTO;
import com.SES.dto.deviceMonitor.DeviceDataRedisDTO;
import com.SES.dto.log.SaveAlertLogDTO;
import com.SES.dto.log.SaveDeviceLogDTO;
import com.SES.service.DeviceApiService;
import com.SES.service.DeviceIdCacheService;
import com.SES.service.DeviceMonitorService;
import com.SES.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;

import static com.SES.config.AutoSettingConfig.ENABLE_AUTO_DEVICE_MONITOR;
import static com.SES.constant.DeviceMonitorConstant.*;
import static com.SES.constant.SimDeviceModeConstant.FAULT_DEVICE_FAILURE;
import static com.SES.constant.SimDeviceModeConstant.FAULT_SHORT_CIRCUIT;

@Service
@Slf4j
public class DeviceMonitorServiceImpl implements DeviceMonitorService {

    @Autowired
    private LogService logService;

    @Autowired
    private DeviceApiService deviceApiService;

    @Autowired
    private DeviceIdCacheService deviceIdCacheService;

    @Autowired
    private RedisTemplate<String, DeviceDataRedisDTO> deviceDataRedisTemplate;



    // 定时任务：每隔一定间隔轮询一次所有设备
    @Scheduled(fixedRate = DEVICE_DATA_REDIS_REFRESH_INTERVAL)
    public void pollAllDevices() {
        if(ENABLE_AUTO_DEVICE_MONITOR==0){
            return;
        }
        List<Long> allDeviceIds = deviceIdCacheService.getWithSyncRefresh();
        if (allDeviceIds == null || allDeviceIds.isEmpty()) {
            log.warn("未找到任何设备ID，跳过轮询");
            return;
        }
        for (Long deviceId : allDeviceIds) {
            pollDeviceAsync(deviceId);
        }
        log.info("自动轮询设备数据完成");
    }

    // 异步轮询单个设备
    @Async("devicePollerExecutor")
    public void pollDeviceAsync(Long deviceId) {
        try {
            DeviceQueryApiResultDTO result = deviceApiService.deviceQueryApi(deviceId);

            // 构建新数据，每次都设置最新的 lastUpdatedTime
            LocalDateTime endTime = LocalDateTime.now();
            DeviceDataRedisDTO newData = new DeviceDataRedisDTO(
                    deviceId,
                    result.getStatus(),
                    result.getModeName(),
                    result.getPower(),
                    endTime
            );

            String cacheKey = REDIS_KEY_PREFIX + deviceId;

            DeviceDataRedisDTO oldData = deviceDataRedisTemplate.opsForValue().get(cacheKey);

            // 更新 Redis 缓存并重置过期时间
            deviceDataRedisTemplate.opsForValue().set(cacheKey, newData, DEVICE_DATA_REDIS_TTL, TimeUnit.MILLISECONDS);

            // 计算时间段起始
            LocalDateTime startTime;
            if (oldData != null && oldData.getLastUpdatedTime().isAfter(
                    endTime.minus(DEVICE_DATA_REDIS_REFRESH_INTERVAL, ChronoUnit.MILLIS))) {
                startTime = oldData.getLastUpdatedTime();
            } else {
                // 最多往前推相当于轮询间隔的时长（使用毫秒）
                startTime = endTime.minus(DEVICE_DATA_REDIS_REFRESH_INTERVAL, ChronoUnit.MILLIS);
            }

            // 如果设备数据发生变化，记录设备日志
            if (newData.isChanged(oldData)) {

                SaveDeviceLogDTO saveDeviceLogDTO = new SaveDeviceLogDTO();
                saveDeviceLogDTO.setDeviceId(deviceId);
                saveDeviceLogDTO.setStartTime(startTime);
                saveDeviceLogDTO.setEndTime(endTime);
                saveDeviceLogDTO.setStatus(newData.getStatus());
                saveDeviceLogDTO.setModeName(newData.getModeName());
                saveDeviceLogDTO.setPower(newData.getPower());

                // 用电量由 LogService 自动计算
                logService.saveDeviceLog(saveDeviceLogDTO);
            }

            // 如果设备故障状态发生变化，记录警报日志
            if (newData.isDeviceFaultChanged(oldData)) {
                if (newData.getStatus().equals(DeviceStatusConstant.FAULT)) {
                    // 设备发生故障，根据类型记录日志
                    String modeName = newData.getModeName();

                    SaveAlertLogDTO saveAlertLogDTO = new SaveAlertLogDTO();
                    saveAlertLogDTO.setDeviceId(deviceId);
                    saveAlertLogDTO.setTime(endTime);
                    saveAlertLogDTO.setStatus(newData.getStatus());
                    saveAlertLogDTO.setModeName(modeName);

                    if (FAULT_SHORT_CIRCUIT.equals(modeName)) {
                        log.info("设备 {} 发生 {} ", deviceId,FAULT_SHORT_CIRCUIT); // 短路

                        saveAlertLogDTO.setLevel(AlertLevelConstant.ALERT_LEVEL_ERROR); // 严重警告
                        saveAlertLogDTO.setMessage("设备发生"+FAULT_SHORT_CIRCUIT);

                    } else if (FAULT_DEVICE_FAILURE.equals(modeName)) {
                        log.info("设备 {} 发生 {} ", deviceId,FAULT_DEVICE_FAILURE); // 设备故障

                        saveAlertLogDTO.setLevel(AlertLevelConstant.ALERT_LEVEL_WARNING); // 警告
                        saveAlertLogDTO.setMessage("设备发生"+FAULT_DEVICE_FAILURE);
                    }

                    logService.saveAlertLog(saveAlertLogDTO);
                } else {
                    // 设备恢复正常
                    SaveAlertLogDTO saveAlertLogDTO = new SaveAlertLogDTO();
                    saveAlertLogDTO.setDeviceId(deviceId);
                    saveAlertLogDTO.setTime(endTime);
                    saveAlertLogDTO.setStatus(newData.getStatus());
                    saveAlertLogDTO.setModeName(newData.getModeName());

                    saveAlertLogDTO.setLevel(AlertLevelConstant.ALERT_LEVEL_RESTORED); // 恢复正常
                    saveAlertLogDTO.setMessage("设备恢复正常");

                    logService.saveAlertLog(saveAlertLogDTO);
                }
            }




        } catch (Exception e) {
            log.error("轮询设备 {} 数据时发生异常", deviceId, e);
        }
    }
}