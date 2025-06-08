package com.SES.service.impl;

import com.SES.constant.CacheConstant;
import com.SES.dto.log.LogCommonDTO;
import com.SES.entity.Device;
import com.SES.entity.Policy;
import com.SES.entity.User;
import com.SES.mapper.DeviceMapper;
import com.SES.mapper.PolicyMapper;
import com.SES.mapper.UserMapper;
import com.SES.service.DeviceIdCacheService;
import com.SES.service.LogCommonCacheService;
import com.SES.service.PolicyService;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LogCommonCacheServiceImpl implements LogCommonCacheService {

    private com.github.benmanes.caffeine.cache.Cache<Long, LogCommonDTO> cache;


    @Autowired
    private PolicyService policyService;

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceIdCacheService deviceIdCacheService;


    /**
     * 初始化缓存
     */
    @PostConstruct
    public void init() {
        cache = Caffeine.newBuilder()
                .maximumSize(1000) // 根据实际情况调整最大缓存数
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();

        refreshAllDeviceCaches(); // 初始加载
    }

    /**
     * 定时任务：每5分钟，刷新所有设备的缓存
     */
    @Scheduled(fixedRate = CacheConstant.LOG_COMMON_REFRESH_INTERVAL)
    public void scheduledRefreshLogCommonCache() {
        log.info("正在执行定时刷新 LogCommonCache...");
        refreshAllDeviceCaches();
    }

    /**
     * 为每个设备id刷新一次缓存
     */
    private void refreshAllDeviceCaches() {
        List<Long> allDeviceIds = deviceIdCacheService.getAllDeviceId();
        if (allDeviceIds == null || allDeviceIds.isEmpty()) {
            log.warn("未找到任何设备ID，跳过 LogCommonCache 刷新");
            return;
        }

        for (Long deviceId : allDeviceIds) {
            refreshLogCommonCache(deviceId);
        }

        log.info("已刷新所有设备的 LogCommonCache，共 {} 个设备", allDeviceIds.size());
    }

    /**
     * 刷新整个缓存项
     */
    public void refreshLogCommonCache(Long deviceId) {
        if (deviceId == null) return;

        try {
            // 查询设备信息
            Device device = deviceMapper.getById(deviceId);
            if (device == null) {
                cache.invalidate(deviceId);
                return;
            }

            // 用户信息
            String username = null;
            if (device.getUserId() != null) {
                User user = userMapper.getById(device.getUserId());
                username = user != null ? user.getUsername() : null;
            }

            // 策略信息
            String policyName = null;
            String policyJson = null;
            if (device.getPolicyId() != null) {
                Policy policy = policyMapper.getById(device.getPolicyId());
                if (policy != null) {
                    policyName = policy.getName();
                    policyJson = policyService.getJsonString(device.getPolicyId()); // 使用方法获取 JSON
                }
            }

            // 构建 DTO
            LogCommonDTO dto = new LogCommonDTO(
                    device.getUserId(),
                    username,
                    device.getName(),
                    policyName,
                    policyJson
            );

            // 更新缓存
            cache.put(deviceId, dto);
            log.debug("已刷新设备 {} 的 LogCommonDTO", deviceId);

        } catch (Exception e) {
            log.error("刷新设备 {} 的 LogCommonDTO 失败", deviceId, e);
        }
    }

    /**
     * 刷新用户名部分
     * 消息体应包含：deviceId, username
     */
    public void refreshLogCommonCacheUser(Long deviceId, String username) {
        if (deviceId == null) return;

        cache.asMap().computeIfPresent(deviceId, (key, oldDto) -> {
            return new LogCommonDTO(
                    oldDto.getUserId(),
                    username,
                    oldDto.getDeviceName(),
                    oldDto.getPolicyName(),
                    oldDto.getPolicyJson()
            );
        });

        log.debug("已刷新设备 {} 的 username: {}", deviceId, username);
    }

    /**
     * 刷新设备名称部分
     * 消息体应包含：deviceId, deviceName
     */
    public void refreshLogCommonCacheDevice(Long deviceId, String deviceName) {
        if (deviceId == null) return;

        cache.asMap().computeIfPresent(deviceId, (key, oldDto) -> {
            return new LogCommonDTO(
                    oldDto.getUserId(),
                    oldDto.getUsername(),
                    deviceName,
                    oldDto.getPolicyName(),
                    oldDto.getPolicyJson()
            );
        });

        log.debug("已刷新设备 {} 的 deviceName: {}", deviceId, deviceName);
    }

    /**
     * 刷新策略部分：policyName，policyJson
     * 消息体应包含：deviceId, policyId
     */
    public void refreshLogCommonCachePolicy(Long deviceId, Long policyId) {
        if (deviceId == null || policyId == null) return;

        Policy policy = policyMapper.getById(policyId);
        String policyName = policy != null ? policy.getName() : null;
        String policyJson = policy != null ? policyService.getJsonString(policyId) : null; // 获取 JSON

        cache.asMap().computeIfPresent(deviceId, (key, oldDto) -> {
            return new LogCommonDTO(
                    oldDto.getUserId(),
                    oldDto.getUsername(),
                    oldDto.getDeviceName(),
                    policyName,
                    policyJson
            );
        });

        log.debug("已刷新设备 {} 的 policy: {}", deviceId, policyName);
    }

    /**
     * 获取缓存的LogCommonDTO
     *
     * @param deviceId 设备ID
     * @return LogCommonDTO
     */
    public LogCommonDTO getLogCommonDTO(Long deviceId) {
        if (deviceId == null) return null;
        return cache.getIfPresent(deviceId);
    }


}