package com.SES.service;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 缓存类抽象接口
 */
@Slf4j
public abstract class AbstractCacheService<K, V> {

    protected final Cache<K, V> cache;

    public AbstractCacheService() {
        this.cache = Caffeine.newBuilder()
                .maximumSize(getMaxSize())
                .expireAfterWrite(getTTLInMillis(), TimeUnit.MILLISECONDS)
                .build();
    }

    // 默认配置
    protected int getMaxSize() {
        return 100;
    }

    protected long getTTLInMillis() {
        return 5 * 60 * 1000; // 单位毫秒
    }

    protected int getRetryTimes() {
        return 3;
    }

    protected boolean enableFallback() {
        return false;
    }

    // 生成兜底数据，可以为空
    protected V createFallback(K key) {
        return null;
    }

    // 缓存获取方法
    public V get(K key) {
        return cache.getIfPresent(key);
    }

    public V getWithSyncRefresh(K key) {
        V value = get(key);
        if (value != null) return value;

        int retry = getRetryTimes();
        while (retry-- > 0) {
            refresh(key);
            value = get(key);
            if (value != null) return value;
        }

        return null;
    }

    public V getWithSyncRefreshAndFallback(K key) {
        V value = getWithSyncRefresh(key);
        if (value == null) {
            value = createFallback(key);

            if(!enableFallback()){
                log.warn("该缓存方法无兜底数据，返回null");
            }
        }
        return value;
    }

    // 刷新方法
    protected abstract void refresh(K key);
}
