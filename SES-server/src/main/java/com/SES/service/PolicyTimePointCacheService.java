package com.SES.service;

import com.SES.dto.log.LogCommonDTO;

import java.time.LocalTime;
import java.util.List;

public interface PolicyTimePointCacheService {


    List<LocalTime> get(Long deviceId);

    List<LocalTime> getWithSyncRefresh(Long deviceId);

    /**
     * 供外部调用的同步刷新方法
     * @param deviceId 设备ID
     */
    public void refreshSync(Long deviceId);
}

