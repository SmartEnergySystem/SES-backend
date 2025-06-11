package com.SES.dto.deviceMonitor;

import com.SES.constant.DeviceStatusConstant;
import com.SES.dto.device.DeviceStatusEditDTO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "redis储存设备当前数据的模型")
@Slf4j
public class DeviceDataRedisDTO {
    private Long deviceId;
    private Integer status;
    private String modeName;
    private Float power;
    private LocalDateTime lastUpdatedTime; // 最后更新时间

    public boolean isChanged(DeviceDataRedisDTO other) {
        if (other == null) {
            return true;
        }

        return !Objects.equals(this.status, other.status) ||
                !Objects.equals(this.modeName, other.modeName) ||
                Math.abs(this.power - other.power) > 0.01f;
    }

    /**
     * 判断是否发生了故障状态变化：从故障到正常 或 从正常到故障
     */
    public boolean isDeviceFaultChanged(DeviceDataRedisDTO other) {
        if (other == null) {
            return true;
        }

        // 当前状态和之前状态
        Integer currentStatus = this.status;
        Integer previousStatus = other.status;
        //log.info("设备ID: {}, 当前状态: {}, 上一状态: {}", this.deviceId, currentStatus, previousStatus);



        // 如果当前是故障(-1)，而之前不是故障，则状态发生了故障变化
        if (currentStatus.equals(DeviceStatusConstant.FAULT) && !previousStatus.equals(DeviceStatusConstant.FAULT)) {
            return true;
        }

        // 如果当前不是故障，而之前是故障，则状态发生了恢复（从故障到正常）
        if (!currentStatus.equals(DeviceStatusConstant.FAULT) && previousStatus.equals(DeviceStatusConstant.FAULT)) {
            return true;
        }

        return false;
    }
}