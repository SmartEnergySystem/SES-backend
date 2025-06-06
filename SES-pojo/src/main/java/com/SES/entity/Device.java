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
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String name;

    private Integer lastKnownStatus; // 0=关闭，1=开启，-1=异常或损坏

    private Long lastKnownModeId;

    private Long defaultModeId;

    private Long policyId; // 可为空

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}