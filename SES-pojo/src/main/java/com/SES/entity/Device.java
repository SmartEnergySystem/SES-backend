package com.SES.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 设备实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 最后已知状态
     */
    private Integer lastKnownStatus;

    /**
     * 最后已知模式ID
     */
    private Long lastKnownModeId;

    /**
     * 默认模式ID
     */
    private Long defaultModeId;

    /**
     * 策略ID
     */
    private Long policyId;
}
