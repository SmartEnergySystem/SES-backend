package com.SES.constant;

/**
 * 缓存管理类使用的常量
 */
public class CacheConstant {
    // TODO:不调试的时候记得开回来
    public static final Integer ENABLE_AUTO_CACHE_REFRESH = 0; // 是否开启缓存自动刷新,0=false,1=true

    public static final Integer DEVICE_CACHE_MAX_SIZE = 1000; // 设备相关缓存的缓存容量上限

    public static final long DEVICE_ID_REFRESH_INTERVAL = 5 * 60 * 1000; // 设备id刷新间隔：5分钟
    public static final long LOG_COMMON_REFRESH_INTERVAL = 5 * 60 * 1000; // 日志常用字段刷新间隔：5分钟
}
