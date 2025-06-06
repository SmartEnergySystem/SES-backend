package com.SES.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String userUsername; // 冗余信息

    private Long deviceId;

    private String deviceName; // 冗余信息

    private LocalDateTime time;

    private Integer isApplyPolicy; // 可为空

    private Integer status; // 可为空

    private String modeName; // 可为空

    private String policyName; // 可为空

    private String policy; // JSON 格式，可为空

    private String batchName; // 可为空
}