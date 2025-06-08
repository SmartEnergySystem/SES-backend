package com.SES.service.impl;

import com.SES.config.RabbitMQConfig;
import com.SES.mapper.DeviceMapper;
import com.SES.service.DeviceIdCacheService;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.SES.constant.CacheConstant.DEVICE_ID_REFRESH_INTERVAL;

@Service
@Slf4j
public class DeviceIdCacheServiceImpl implements DeviceIdCacheService {

    private Cache<String, List<Long>> cache;

    @Autowired
    private DeviceMapper deviceMapper;


    /**
     * 初始化缓存
     */
    @PostConstruct
    public void init() {
        cache = Caffeine.newBuilder()
                .maximumSize(1) // 只缓存一份
                .expireAfterWrite(5, TimeUnit.MINUTES) // TTL
                .build();

        refreshDeviceIdCache(); // 初始加载
    }


    /**
     * 定时任务：每隔固定时间自动刷新设备ID缓存
     */
    @Scheduled(fixedRate = DEVICE_ID_REFRESH_INTERVAL)
    public void scheduledRefreshDeviceIdCache() {
        log.info("开始自动刷新设备ID缓存...");
        refreshDeviceIdCache();
    }

    /**
     * 监听 RabbitMQ 消息，当收到指定消息时强制刷新设备ID缓存
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_DEVICE_CACHE_REFRESH)
    public void refreshDeviceIdCache() {
        try {
            List<Long> deviceIds = deviceMapper.getAllDeviceIds();
            cache.put("all_device_ids", deviceIds);
            log.info("已刷新设备ID缓存，当前设备数: {}", deviceIds.size());
        } catch (Exception e) {
            log.error("刷新设备ID缓存失败", e);
        }
    }

    /**
     * 获得缓存的所有设备id
     * @return
     */
    public List<Long> getAllDeviceId() {
        return cache.getIfPresent("all_device_ids");
    }
}