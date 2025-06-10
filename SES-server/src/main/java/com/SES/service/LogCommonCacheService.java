package com.SES.service;

import com.SES.dto.log.LogCommonDTO;

public interface LogCommonCacheService {

    LogCommonDTO get(Long deviceId);

    LogCommonDTO getWithSyncRefresh(Long deviceId);

    LogCommonDTO getWithSyncRefreshAndFallback(Long deviceId);
}

