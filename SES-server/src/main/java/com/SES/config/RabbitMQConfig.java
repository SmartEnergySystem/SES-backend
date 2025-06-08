package com.SES.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_DEVICE_CACHE_REFRESH = "device.queue.cache.refresh";
    public static final String EXCHANGE_DEVICE = "deviceExchange";
    public static final String ROUTING_KEY_CACHE_REFRESH = "device.cache.refresh";

    @Bean
    public Queue deviceCacheRefreshQueue() {
        return new Queue(QUEUE_DEVICE_CACHE_REFRESH);
    }

    @Bean
    public DirectExchange deviceExchange() {
        return new DirectExchange(EXCHANGE_DEVICE);
    }

    @Bean
    public Binding bindingDeviceCacheRefresh(DirectExchange deviceExchange, Queue deviceCacheRefreshQueue) {
        return BindingBuilder.bind(deviceCacheRefreshQueue)
                .to(deviceExchange)
                .with(ROUTING_KEY_CACHE_REFRESH);
    }
}