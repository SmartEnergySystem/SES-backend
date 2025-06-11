package com.SES.constant;

/**
 * 缓存管理类使用的常量
 */
public class CacheConstant {




    //===deviceId缓存相关===
    public static final String DEVICE_ID_CACHE_KEY = "all_device_ids"; // 单个缓存项，需要默认key
    public static final int DEVICE_ID_CACHE_MAX_SIZE = 1; // 缓存容量上限
    public static final long DEVICE_ID_CACHE_TTL = 5 * 60 * 1000; // 缓存过期时间：5分钟
    public static final int DEVICE_ID_CACHE_RETRY_TIMES = 3; // 缓存缺失时的尝试次数
    public static final long DEVICE_ID_CACHE_REFRESH_INTERVAL = 5 * 60 * 1000; // 自动刷新间隔：5分钟

    //===LogCommon缓存相关===
    public static final int LOG_COMMON_CACHE_MAX_SIZE = 1000; // 缓存容量上限
    public static final long LOG_COMMON_CACHE_TTL = 5 * 60 * 1000; // 缓存过期时间：5分钟
    public static final int LOG_COMMON_CACHE_RETRY_TIMES = 3; // 缓存缺失时的尝试次数
    public static final long LOG_COMMON_CACHE_REFRESH_INTERVAL = 5 * 60 * 1000; // 自动刷新间隔：5分钟

    //===PolicyTimePoint缓存相关===
    public static final int POLICY_TIME_POINT_CACHE_MAX_SIZE = 1000; // 缓存容量上限
    public static final long POLICY_TIME_POINT_CACHE_TTL = 30 * 60 * 1000; // 缓存过期时间：30分钟
    public static final int POLICY_TIME_POINT_CACHE_RETRY_TIMES = 3; // 缓存缺失时的尝试次数
    public static final long POLICY_TIME_POINT_CACHE_REFRESH_INTERVAL = 30 * 60 * 1000; // 自动刷新间隔：30分钟
}
