package com.SES.service.impl;

import com.SES.config.RabbitMQConfig;
import com.SES.mapper.DeviceMapper;
import com.SES.service.DeviceIdCacheService;
import com.SES.service.AbstractCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.rabbitmq.client.Channel;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

import static com.SES.config.AutoSettingConfig.ENABLE_AUTO_CACHE_REFRESH;
import static com.SES.constant.CacheConstant.*;

@Service
@Slf4j
public class DeviceIdCacheServiceImpl extends AbstractCacheService<String, List<Long>> implements DeviceIdCacheService {



    @Autowired
    private DeviceMapper deviceMapper;

    // 默认配置
    @Override
    protected int getMaxSize() {
        return DEVICE_ID_CACHE_MAX_SIZE;
    }

    @Override
    protected long getTTLInMillis() {
        return DEVICE_ID_CACHE_TTL; // 单位毫秒
    }

    @Override
    protected int getRetryTimes() {
        return DEVICE_ID_CACHE_RETRY_TIMES;
    }

    // 无兜底数据
    @Override
    protected boolean enableFallback() {
        return false;
    }

    /**
     * 初始化缓存
     */
    @PostConstruct
    public void init() {
        refresh(DEVICE_ID_CACHE_KEY);
    }

    /**
     * 定时自动刷新设备ID缓存
     */
    @Scheduled(fixedRate = DEVICE_ID_CACHE_REFRESH_INTERVAL)
    public void scheduledRefreshDeviceIdCache() {
        if (ENABLE_AUTO_CACHE_REFRESH == 0) {
            return;
        }
        log.info("开始自动刷新设备ID缓存...");
        refresh(DEVICE_ID_CACHE_KEY);
    }

    /**
     * 监听 RabbitMQ 消息，当收到指定消息时刷新缓存
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_DEVICE_CACHE_REFRESH)
    public void onRefreshDeviceIdCache(Message message, Channel channel) throws IOException {
        try {
            log.info("收到刷新设备ID请求");
            refresh(DEVICE_ID_CACHE_KEY);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("刷新设备ID缓存失败", e);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    /**
     * 刷新缓存的具体实现
     */
    @Override
    protected void refresh(String key) {
        log.info("开始刷新设备ID缓存...");
        try {
            List<Long> deviceIds = deviceMapper.getAllDeviceIds();
            cache.put(key, deviceIds);
            log.info("已刷新设备ID缓存，当前设备数: {}", deviceIds.size());
        } catch (Exception e) {
            log.error("刷新设备ID缓存失败", e);
        }
    }

    /**
     * 无参 get：获取默认 key 的缓存数据
     */
    public List<Long> get() {
        return get(DEVICE_ID_CACHE_KEY);
    }

    /**
     * 无参 get：同步刷新版本
     */
    public List<Long> getWithSyncRefresh() {
        return super.getWithSyncRefresh(DEVICE_ID_CACHE_KEY);
    }
}