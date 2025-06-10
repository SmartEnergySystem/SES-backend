package com.SES.dto.deviceMonitor;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "redis储存设备当前数据的模型")
public class DeviceDataRedisDTO {
    private Long deviceId;
    private Integer status;
    private String modeName;
    private Float power;
    private LocalDateTime lastUpdatedTime; // 最后更新时间

    public boolean isStateChanged(DeviceDataRedisDTO other) {
        if (other == null) {
            return true;
        }

        return !Objects.equals(this.status, other.status) ||
                !Objects.equals(this.modeName, other.modeName) ||
                Math.abs(this.power - other.power) > 0.01f;
    }
}