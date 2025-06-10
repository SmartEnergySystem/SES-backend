package com.SES.dto.log;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeviceLogDataDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private String modeName;
    private String policyName;
    private Float power;
    private Float energyConsumption;
}
