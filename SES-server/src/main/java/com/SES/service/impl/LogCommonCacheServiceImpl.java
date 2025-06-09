package com.SES.service.impl;

import com.SES.config.RabbitMQConfig;
import com.SES.constant.CacheConstant;
import com.SES.dto.log.LogCommonDTO;
import com.SES.dto.logCommonCache.RefreshLogCommonCacheDeviceMessageDTO;
import com.SES.dto.logCommonCache.RefreshLogCommonCacheUserMessageDTO;
import com.SES.dto.logCommonCache.RefreshLogCommonCachePolicyMessageDTO;
import com.SES.dto.logCommonCache.RefreshLogCommonCacheMessageDTO;
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
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.core.Message;
import com.rabbitmq.client.Channel;                // RabbitMQ 通道对象


import static com.SES.constant.CacheConstant.DEVICE_CACHE_MAX_SIZE;
import static com.SES.constant.CacheConstant.ENABLE_AUTO_CACHE_REFRESH;

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

    @Autowired
    private MessageConverter messageConverter;

    /**
     * 初始化缓存
     */
    @PostConstruct
    public void init() {
        cache = Caffeine.newBuilder()
                .maximumSize(DEVICE_CACHE_MAX_SIZE) // 根据实际情况调整最大缓存数
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();

        refreshAllDeviceCaches(); // 初始加载
    }

    /**
     * 定时任务：每5分钟，刷新所有设备的缓存
     */
    @Scheduled(fixedRate = CacheConstant.LOG_COMMON_REFRESH_INTERVAL)
    public void scheduledRefreshLogCommonCache() {
        if(ENABLE_AUTO_CACHE_REFRESH==0){
            return;
        }
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
     * 刷新deviceId的整个缓存项
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
     * 监听来自“刷新 LogCommon 缓存”的消息
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_LOG_COMMON_REFRESH)
    public void onRefreshLogCommonCache(RefreshLogCommonCacheMessageDTO dto,
                                        Message message, Channel channel) throws Exception {
        try {
            log.info("收到刷新 LogCommon 缓存请求: {}", dto);

            if (dto.getDeviceId() == null) {
                log.warn("消息格式错误，缺少 deviceId");
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            // 调用刷新方法
            refreshLogCommonCache(dto.getDeviceId());

            // 手动确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            log.error("处理刷新 LogCommon 缓存消息失败", e);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }


    /**
     * 监听来自“修改用户名”的消息
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_LOG_COMMON_REFRESH_USER)
    public void onRefreshLogCommonCacheByUserId(RefreshLogCommonCacheUserMessageDTO dto,
                                                Message message, Channel channel) throws Exception {
        try {
            log.info("收到刷新LogCommon用户名缓存请求: {}", dto);

            if (dto.getUserId() == null || dto.getUsername() == null) {
                log.warn("消息格式错误，缺少 userId 或 username");
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            refreshLogCommonCacheUser(dto.getUserId(), dto.getUsername());

            // 手动确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            log.error("处理刷新用户名缓存消息失败", e);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    /**
     * 刷新用户名部分
     * 消息体应包含：userId, username
     * 直接修改缓存中该用户所有设备的 username（基于缓存查找）
     */
    public void refreshLogCommonCacheUser(Long userId, String newUsername) {
        log.info("开始刷新LogCommon用户 {} 的所有设备 username...", userId);
        if (userId == null || newUsername == null) {
            return;
        }

        cache.asMap().forEach((deviceId, oldDto) -> {
            if (oldDto != null && userId.equals(oldDto.getUserId())) {
                LogCommonDTO updatedDto = new LogCommonDTO(
                        oldDto.getUserId(),
                        newUsername,
                        oldDto.getDeviceName(),
                        oldDto.getPolicyName(),
                        oldDto.getPolicyJson()
                );
                cache.put(deviceId, updatedDto);
                log.debug("已刷新LogCommon设备 {} 的 username 为 {}", deviceId, newUsername);
            }
        });

        log.info("已完成LogCommon用户 {} 的所有设备 username 刷新", userId);
    }


    /**
     * 监听来自“修改设备名称”的消息
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_LOG_COMMON_REFRESH_DEVICE)
    public void onRefreshLogCommonCacheByDevice(RefreshLogCommonCacheDeviceMessageDTO dto,
                                                Message message, Channel channel) throws Exception {
        try {
            log.info("收到刷新LogCommon设备名称缓存请求: {}", dto);

            if (dto.getDeviceId() == null || dto.getDeviceName() == null) {
                log.warn("消息格式错误，缺少 deviceId 或 deviceName");
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            // 调用刷新方法
            refreshLogCommonCacheDevice(dto.getDeviceId(), dto.getDeviceName());

            // 手动确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            log.error("处理刷新设备名称缓存消息失败", e);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    /**
     * 刷新设备名称部分
     * 消息体应包含：deviceId, deviceName
     */
    public void refreshLogCommonCacheDevice(Long deviceId, String deviceName) {
        log.info("开始刷新LogCommon设备 {} 的名称...", deviceId);
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

        log.info("已刷新LogCommon设备 {} 的 deviceName: {}", deviceId, deviceName);
    }


    /**
     * 监听来自“策略变更”的消息
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_LOG_COMMON_REFRESH_POLICY)
    public void onRefreshLogCommonCacheByPolicy(RefreshLogCommonCachePolicyMessageDTO dto,
                                                Message message, Channel channel) throws Exception {
        try {
            log.info("收到刷新LogCommon策略缓存请求: {}", dto);

            if (dto.getDeviceId() == null) {
                log.warn("消息格式错误，缺少 deviceId");
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            // 调用刷新方法
            refreshLogCommonCachePolicy(dto.getDeviceId(), dto.getPolicyId());

            // 手动确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            log.error("处理刷新策略缓存消息失败", e);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    /**
     * 刷新策略部分：policyName，policyJson
     * 消息体应包含：deviceId, policyId
     * 需要重新查策略表
     */
    public void refreshLogCommonCachePolicy(Long deviceId, Long policyId) {
        if (deviceId == null) return;

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

        log.info("已刷新LogCommon设备 {} 的 policy: {}", deviceId, policyName);
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