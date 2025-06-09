package com.SES.constant;

/**
 * 设备监测类使用的常量
 */
public class DeviceMonitorConstant {
    // TODO:不调试的时候记得开回来
    public static final Integer ENABLE_AUTO_DEVICE_MONITOR = 0; // 是否开启设备自动监测,0=false,1=true

    public static final long DEVICE_QUERY_INTERVAL = 5 * 1000; // 设备轮询间隔：5秒（单位毫秒）

    public static final long DEVICE_DATA_REDIS_TTL = 60 * 1000; // redis设备数据过期间隔：60秒

    // 数据过期时间（用于判断是否为实时数据）：2 * 轮询间隔 = 10秒
    private static final long DEVICE_DATA_EXPIRATION_TTL = DEVICE_QUERY_INTERVAL * 2;
}
