package com.SES.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RabbitMQConfig {

    // ==== 交换机 ====
    public static final String EXCHANGE_DEVICE = "deviceExchange";

    // ==== DeviceIdCache刷新相关====
    public static final String QUEUE_DEVICE_CACHE_REFRESH = "device.queue.cache.refresh";
    public static final String ROUTING_KEY_CACHE_REFRESH = "device.cache.refresh";


    // ==== LogCommonCache刷新相关 ====
    public static final String QUEUE_LOG_COMMON_REFRESH = "log.common.refresh";
    public static final String ROUTING_KEY_REFRESH = "log.common.refresh";

    // ==== LogCommonCacheUser刷新相关====
    public static final String QUEUE_LOG_COMMON_REFRESH_USER = "log.common.refresh.user";
    public static final String ROUTING_KEY_REFRESH_USER = "log.common.refresh.user";

    // ==== LogCommonCacheDevice刷新相关====
    public static final String QUEUE_LOG_COMMON_REFRESH_DEVICE = "log.common.refresh.device";
    public static final String ROUTING_KEY_REFRESH_DEVICE = "log.common.refresh.device";

    // ==== LogCommonCachePolicy刷新相关 ====
    public static final String QUEUE_LOG_COMMON_REFRESH_POLICY = "log.common.refresh.policy";
    public static final String ROUTING_KEY_REFRESH_POLICY = "log.common.refresh.policy";


    // ==== 创建 Bean ====

    @Bean
    @Primary  // 标记为主 Bean，避免多个 MessageConverter 导致冲突
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter(); // 支持 JSON 消息解析
    }


    @Bean
    public DirectExchange deviceExchange() {
        return new DirectExchange(EXCHANGE_DEVICE);
    }

    @Bean
    public Queue deviceCacheRefreshQueue() {
        return new Queue(QUEUE_DEVICE_CACHE_REFRESH);
    }

    @Bean
    public Queue logCommonRefreshUserQueue() {
        return new Queue(QUEUE_LOG_COMMON_REFRESH_USER);
    }

    @Bean
    public Queue logCommonRefreshDeviceQueue() {
        return new Queue(QUEUE_LOG_COMMON_REFRESH_DEVICE);
    }

    @Bean
    public Queue logCommonRefreshPolicyQueue() {
        return new Queue(QUEUE_LOG_COMMON_REFRESH_POLICY);
    }

    @Bean
    public Queue logCommonRefreshQueue() {
        return new Queue(QUEUE_LOG_COMMON_REFRESH);
    }


    @Bean
    public Binding bindingDeviceCacheRefresh(DirectExchange deviceExchange, Queue deviceCacheRefreshQueue) {
        return BindingBuilder.bind(deviceCacheRefreshQueue)
                .to(deviceExchange)
                .with(ROUTING_KEY_CACHE_REFRESH);
    }

    @Bean
    public Binding bindingLogCommonRefreshUser(DirectExchange deviceExchange, Queue logCommonRefreshUserQueue) {
        return BindingBuilder.bind(logCommonRefreshUserQueue)
                .to(deviceExchange)
                .with(ROUTING_KEY_REFRESH_USER);
    }

    @Bean
    public Binding bindingLogCommonRefreshDevice(DirectExchange logicalExchange, Queue logCommonRefreshDeviceQueue) {
        return BindingBuilder.bind(logCommonRefreshDeviceQueue)
                .to(logicalExchange)
                .with(ROUTING_KEY_REFRESH_DEVICE);
    }

    @Bean
    public Binding bindingLogCommonRefreshPolicy(DirectExchange logicalExchange, Queue logCommonRefreshPolicyQueue) {
        return BindingBuilder.bind(logCommonRefreshPolicyQueue)
                .to(logicalExchange)
                .with(ROUTING_KEY_REFRESH_POLICY);
    }

    @Bean
    public Binding bindingLogCommonRefresh(DirectExchange logicalExchange, Queue logCommonRefreshQueue) {
        return BindingBuilder.bind(logCommonRefreshQueue)
                .to(logicalExchange)
                .with(ROUTING_KEY_REFRESH);
    }
}