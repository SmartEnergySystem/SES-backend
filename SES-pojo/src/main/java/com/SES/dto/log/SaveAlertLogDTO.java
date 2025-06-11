package com.SES.dto.log;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SaveAlertLogDTO {
    private Long deviceId;
    private LocalDateTime time;
    private Integer level;
    private Integer status;
    private String modeName;
    private String message;
}
