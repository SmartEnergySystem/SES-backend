package com.SES.service.impl;

import com.SES.constant.CacheConstant;
import com.SES.dto.log.LogCommonDTO;
import com.SES.entity.Device;
import com.SES.entity.Policy;
import com.SES.entity.User;
import com.SES.mapper.DeviceMapper;
import com.SES.mapper.PolicyMapper;
import com.SES.mapper.UserMapper;
import com.SES.service.AbstractCacheService;
import com.SES.service.DeviceIdCacheService;
import com.SES.service.PolicyService;
import com.SES.service.PolicyTimePointCacheService;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static com.SES.config.AutoSettingConfig.ENABLE_AUTO_CACHE_REFRESH;
import static com.SES.constant.CacheConstant.*;

@Slf4j
@Service
public class PolicyTimePointCacheServiceImpl extends AbstractCacheService<Long, List<LocalTime>> implements PolicyTimePointCacheService {

    @Autowired
    private PolicyService policyService;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceIdCacheService deviceIdCacheService;

    // 默认配置
    @Override
    protected int getMaxSize() {
        return POLICY_TIME_POINT_CACHE_MAX_SIZE;
    }

    @Override
    protected long getTTLInMillis() {
        return POLICY_TIME_POINT_CACHE_TTL; // 单位毫秒
    }

    @Override
    protected int getRetryTimes() {
        return POLICY_TIME_POINT_CACHE_RETRY_TIMES;
    }

    // 不提供兜底数据
    @Override
    protected boolean enableFallback() {
        return false;
    }


    /**
     * 初始化缓存
     */
    @PostConstruct
    public void init() {
        refreshAllDeviceCaches();
    }

    /**
     * 定时自动刷新所有设备的缓存
     */
    @Scheduled(fixedRate = CacheConstant.LOG_COMMON_CACHE_REFRESH_INTERVAL)
    public void scheduledRefreshLogCommonCache() {
        if(ENABLE_AUTO_CACHE_REFRESH==0){
            return;
        }
        log.info("正在执行定时刷新 PolicyTimePointCache...");
        refreshAllDeviceCaches();
    }

    /**
     * 为每个设备id刷新一次缓存
     */
    private void refreshAllDeviceCaches() {
        List<Long> allDeviceIds = deviceIdCacheService.getWithSyncRefresh();
        if (allDeviceIds == null || allDeviceIds.isEmpty()) {
            log.warn("未找到任何设备ID，跳过 PolicyTimePointCache 刷新");
            return;
        }

        for (Long deviceId : allDeviceIds) {
            refresh(deviceId);
        }

        log.info("已刷新所有设备的 PolicyTimePointCache，共 {} 个设备", allDeviceIds.size());
    }

    /**
     * 刷新deviceId的缓存项
     * @param deviceId
     */
    protected void refresh(Long deviceId) {
        if (deviceId == null) return;

        try {
            // 查询设备信息
            Device device = deviceMapper.getById(deviceId);
            if (device == null) {
                cache.invalidate(deviceId);
                return;
            }

            // 策略信息
            List<LocalTime> timePoint = Collections.emptyList(); // 默认为空列表
            if (device.getPolicyId() != null) {
                timePoint = policyService.getAllTimePointsByPolicyId(device.getPolicyId());
            }

            // 更新缓存
            cache.put(deviceId, timePoint);
            log.debug("已刷新设备 {} 的 PolicyTimePointCacheO", deviceId);

        } catch (Exception e) {
            log.error("刷新设备 {} 的 PolicyTimePointCache 失败", deviceId, e);
        }
    }

    /**
     * 供外部调用的同步刷新方法
     * @param deviceId 设备ID
     */
    public void refreshSync(Long deviceId) {
        refresh(deviceId);
    }


}