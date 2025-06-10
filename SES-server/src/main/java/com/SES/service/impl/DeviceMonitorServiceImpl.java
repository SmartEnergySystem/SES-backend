package com.SES.service.impl;

import com.SES.dto.deviceApi.DeviceQueryApiResultDTO;
import com.SES.dto.deviceMonitor.DeviceDataRedisDTO;
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
            deviceDataRedisTemplate.opsForValue().set(cacheKey, newData, DEVICE_DATA_REDIS_TTL, TimeUnit.SECONDS);


            // 计算时间段起始
            LocalDateTime startTime;
            if (oldData != null && oldData.getLastUpdatedTime().isBefore(
                    endTime.minus(DEVICE_DATA_REDIS_REFRESH_INTERVAL, ChronoUnit.MILLIS))) {
                startTime = oldData.getLastUpdatedTime();
            } else {
                // 最多往前推相当于轮询间隔的时长（使用毫秒）
                startTime = endTime.minus(DEVICE_DATA_REDIS_REFRESH_INTERVAL, ChronoUnit.MILLIS);
            }

            // 只有在状态变化的情况下才记录日志
            if (oldData == null || oldData.isStateChanged(newData)) {

                SaveDeviceLogDTO saveDeviceLogDTO = new SaveDeviceLogDTO();
                saveDeviceLogDTO.setDeviceId(deviceId);
                saveDeviceLogDTO.setStartTime(startTime);
                saveDeviceLogDTO.setEndTime(endTime);
                saveDeviceLogDTO.setStatus(result.getStatus());
                saveDeviceLogDTO.setModeName(result.getModeName());
                saveDeviceLogDTO.setPower(result.getPower());

                // 用电量由 LogService 自动计算
                logService.saveDeviceLog(saveDeviceLogDTO);
            }

            // TODO:报警相关
            // TODO:策略管理类

        } catch (Exception e) {
            log.error("轮询设备 {} 数据时发生异常", deviceId, e);
        }
    }
}