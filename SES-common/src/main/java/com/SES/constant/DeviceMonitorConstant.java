package com.SES.constant;

/**
 * 设备监测类使用的常量
 */
public class DeviceMonitorConstant {

    public static final long DEVICE_DATA_REDIS_TTL = 60 * 1000; // redis设备数据过期间隔：60秒
    public static final long DEVICE_DATA_REDIS_REFRESH_INTERVAL = 5 * 1000; // 设备轮询间隔：5秒（单位毫秒）



    // 数据过期时间（用于判断是否为实时数据）：2 * 轮询间隔 = 10秒
    public static final long DEVICE_DATA_EXPIRATION_TTL = DEVICE_DATA_REDIS_REFRESH_INTERVAL * 2;


    public static final String REDIS_KEY_PREFIX = "device:data:"; // 设备数据redis缓存的键名前缀

}
