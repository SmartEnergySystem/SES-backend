package com.SES.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userUsername;

    /**
     * 设备ID
     */
    private Long deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 操作时间
     */
    private LocalDateTime time;

    /**
     * 是否应用策略
     */
    private Integer isApplyPolicy;

    /**
     * 设备状态
     */
    private Integer status;

    /**
     * 模式名称
     */
    private String modeName;

    /**
     * 策略名称
     */
    private String policyName;

    /**
     * 策略详情（JSON格式）
     */
    private String policy;

    /**
     * 批量操作名称
     */
    private String batchName;
}
