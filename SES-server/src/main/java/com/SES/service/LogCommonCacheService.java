package com.SES.service;

import com.SES.dto.log.LogCommonDTO;

public interface LogCommonCacheService {

    /**
     * 获取缓存的LogCommonDTO
     *
     * @param deviceId 设备ID
     * @return LogCommonDTO
     */
    LogCommonDTO getLogCommonDTO(Long deviceId);
}
