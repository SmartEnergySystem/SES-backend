package com.SES.service.impl;

import com.SES.config.RabbitMQConfig;
import com.SES.service.DeviceIdCacheService;
import com.SES.service.PolicyMonitorService;
import com.SES.service.PolicyTaskFactoryService;
import com.SES.service.PolicyTimePointCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import org.springframework.amqp.core.Message;
import com.rabbitmq.client.Channel;

import static com.SES.config.AutoSettingConfig.ENABLE_AUTO_POLICY_MONITOR;
import static com.SES.constant.PolicyMonitorConstant.POLICY_TASK_REFRESH_INTERVAL;


@Service
@Slf4j
public class PolicyMonitorServiceImpl implements PolicyMonitorService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    // 设备ID → 当前任务
    private final Map<Long, ScheduledFuture<?>> deviceTasks = new ConcurrentHashMap<>();

    @Autowired
    private PolicyTimePointCacheService policyTimePointCacheService;

    @Autowired
    private PolicyTaskFactoryService policyTaskFactoryService;

    @Autowired
    private DeviceIdCacheService deviceIdCacheService;


    /**
     * 初始刷新
     */
    @PostConstruct
    public void init() {
        refreshAllDeviceTasks();
    }

    /**
     * 自动刷新
     */
    @Scheduled(fixedRate = POLICY_TASK_REFRESH_INTERVAL)
    public void scheduledRefreshAllDeviceTasks() {
        if (ENABLE_AUTO_POLICY_MONITOR == 0) {
            return;
        }

        log.info("正在执行定时刷新所有设备的策略任务...");
        refreshAllDeviceTasks();
    }

    /**
     * 为每个设备ID刷新一次策略任务
     */
    private void refreshAllDeviceTasks() {
        List<Long> allDeviceIds = deviceIdCacheService.getWithSyncRefresh();
        if (allDeviceIds == null || allDeviceIds.isEmpty()) {
            log.warn("未找到任何设备ID，跳过策略任务刷新");
            return;
        }

        for (Long deviceId : allDeviceIds) {
            refreshTaskByDeviceId(deviceId);
        }

        log.info("已刷新所有设备的策略任务，共 {} 个设备", allDeviceIds.size());
    }

    /**
     * 刷新deviceId的任务
     */
    public void refreshTaskByDeviceId(Long deviceId) {
        // 如果有旧任务则取消
        cancelCurrentTask(deviceId);

        // 获取时间点列表
        List<LocalTime> schedule = policyTimePointCacheService.getWithSyncRefresh(deviceId);
        if (schedule == null || schedule.isEmpty()) {
            log.info("设备 {} 无策略任务，跳过任务安排", deviceId);
            return;
        }

        LocalTime nextTime = findNextExecutionTime(schedule);
        if (nextTime == null) {
            log.info("设备 {} 在今天无更多策略任务", deviceId);
            return;
        }

        Duration duration = Duration.between(LocalTime.now(), nextTime);
        long delayMillis = Math.max(0, duration.toMillis());

        ScheduledFuture<?> future = scheduler.schedule(
                policyTaskFactoryService.create(deviceId, nextTime),
                delayMillis,
                TimeUnit.MILLISECONDS);

        deviceTasks.put(deviceId, future);
        log.info("已刷新设备 {} 在时间点 {} 的策略任务", deviceId, nextTime);
    }

    /**
     * 取消deviceId的当前任务
     */
    public void cancelCurrentTask(Long deviceId) {
        ScheduledFuture<?> oldTask = deviceTasks.remove(deviceId);
        if (oldTask != null && !oldTask.isDone()) {
            oldTask.cancel(false);
        }
    }

    /**
     * 查找下一个执行时间点
     */
    private LocalTime findNextExecutionTime(List<LocalTime> times) {
        LocalTime now = LocalTime.now();
        return times.stream()
                .filter(t -> t.isAfter(now))
                .min(LocalTime::compareTo)
                .orElse(null);
    }

    /**
     * 回调:刷新下一个任务
     * @param deviceId
     */
    @Override
    public void scheduleNextTask(Long deviceId) {
        refreshTaskByDeviceId(deviceId);
    }

    /**
     * 监听来自 RabbitMQ 的消息，触发设备任务刷新
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_POLICY_MONITOR_REFRESH)
    public void onRefreshTaskByDeviceIdManualAck(Long deviceId, Message message, Channel channel) throws Exception {
        try {
            log.info("PolicyMonitor接收到策略变更通知，设备ID: {}", deviceId);

            if (deviceId == null || deviceId <= 0) {
                log.warn("接收到无效的设备ID: {}", deviceId);
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            // 刷新缓存并同步等待
            policyTimePointCacheService.refreshSync(deviceId);

            // 刷新任务
            refreshTaskByDeviceId(deviceId);

            // 手动确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            log.error("处理设备 {} 的策略变更消息失败", deviceId, e);
        }
    }
}