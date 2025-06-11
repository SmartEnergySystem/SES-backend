package com.SES.service;

import com.SES.dto.policy.PolicyDTO;
import com.SES.dto.policy.PolicyNameEditDTO;
import com.SES.vo.policy.PolicyVO;

import java.time.LocalTime;
import java.util.List;

public interface PolicyMonitorService {

    /**
     * 用于策略任务的回调
     */
    void scheduleNextTask(Long deviceId);
}
