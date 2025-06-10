package com.SES.service;

import com.SES.dto.log.LogCommonDTO;

import java.util.List;

import static com.SES.constant.CacheConstant.DEVICE_ID_CACHE_KEY;

public interface DeviceIdCacheService {


    /**
     * 无参 get：获取默认 key 的缓存数据
     */
    public List<Long> get();

    /**
     * 无参 get：同步刷新版本
     */
    public List<Long> getWithSyncRefresh();

}

