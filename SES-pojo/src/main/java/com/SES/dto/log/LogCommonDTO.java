package com.SES.dto.log;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "储存设备日志时不常变的字段")
public class LogCommonDTO {
    private Long userId;
    private String username;
    private String deviceName;
    private String policyName;
    private String policyJson; // JSON 字符串更便于存储
}