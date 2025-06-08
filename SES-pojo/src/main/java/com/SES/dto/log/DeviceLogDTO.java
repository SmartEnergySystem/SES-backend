package com.SES.dto.log;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeviceLogDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long deviceId;
    private String deviceName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private String modeName;
    private String policyName;
    private String policy; // JSON 类型字段可以用 String 存储
    private Float power;
    private Integer energyConsumption;
}
