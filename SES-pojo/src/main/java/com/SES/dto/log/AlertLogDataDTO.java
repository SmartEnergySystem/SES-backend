package com.SES.dto.log;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertLogDataDTO {
    private LocalDateTime time;
    private Integer level;
    private Integer status;
    private String modeName;
    private String policyName;
    private String message;
}
