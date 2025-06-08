package com.SES.dto.log;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "储存设备日志时输入的字段")
public class SaveDeviceLogDTO {
    private Long deviceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private String modeName;
    private Float power; // 单位：W
}
