package com.SES.dto.log;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertLogDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long deviceId;
    private String deviceName;
    private LocalDateTime time;
    private Integer level;
    private Integer status;
    private String modeName;
    private String policyName;
    private String policy; // JSON 类型可用 String 存储
    private String message;
}