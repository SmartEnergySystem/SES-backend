package com.SES.service.impl;

import com.SES.dto.policyItem.PolicyTaskResultDTO;
import com.SES.service.DeviceApiService;
import com.SES.service.PolicyMonitorService;
import com.SES.service.PolicyService;
import com.SES.service.PolicyTaskFactoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * 工厂服务类，用于创建 PolicyTask 实例
 */
@Component
@Slf4j
public class PolicyTaskFactoryServiceImpl implements PolicyTaskFactoryService {

    @Autowired
    private PolicyService policyService;

    @Autowired
    private DeviceApiService deviceApiService;

    @Autowired
    private PolicyMonitorService policyMonitorService;

    /**
     * 工厂方法：创建一个任务实例
     */
    @Override
    public Runnable create(Long deviceId, LocalTime timePoint) {
        return new PolicyTask(deviceId, timePoint, policyService, deviceApiService, policyMonitorService);
    }

    /**
     * 静态内部类：实际的任务逻辑
     */
    private static class PolicyTask implements Runnable {

        private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PolicyTask.class);

        private final Long deviceId;
        private final PolicyService policyService;
        private final DeviceApiService deviceApiService;
        private final LocalTime timePoint;
        private final PolicyMonitorService policyMonitorService;

        public PolicyTask(
                Long deviceId,
                LocalTime timePoint,
                PolicyService policyService,
                DeviceApiService deviceApiService,
                PolicyMonitorService policyMonitorService) { // 构造器注入
            this.deviceId = deviceId;
            this.policyService = policyService;
            this.deviceApiService = deviceApiService;
            this.timePoint = timePoint;
            this.policyMonitorService = policyMonitorService;
        }

        /**
         * 实际任务逻辑
         * 任务结束刷新下一个任务
         */
        @Override
        public void run() {
            log.info("开始执行设备 {} 在时间点 {} 的策略任务", deviceId, timePoint);
            try {
                // 获取任务内容
                PolicyTaskResultDTO result = policyService.getPolicyTaskByPolicyIdAndStartTime(deviceId, timePoint);

                if (result == null) {
                    log.warn("设备 {} 在时间点 {} 无有效策略任务", deviceId, timePoint);
                    return;
                }

                // 调用设备控制API
                deviceApiService.deviceControlApi(deviceId, result.getStatus(), result.getModeName());

            } catch (Exception e) {
                log.error("执行设备 {} 的策略任务失败", deviceId, e);
            }finally {
                // 使用接口回调触发下一次任务刷新
                policyMonitorService.scheduleNextTask(deviceId);
            }
        }
    }
}