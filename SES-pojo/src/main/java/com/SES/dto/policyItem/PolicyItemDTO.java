package com.SES.dto.policyItem;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * 新增策略条目时传递的数据模型
 */
@Data
@ApiModel(description = "新增策略条目时传递的数据模型")
public class PolicyItemDTO implements Serializable {

    @ApiModelProperty("策略ID")
    private Long policyId;

    @ApiModelProperty("开始时间，格式：HH:mm:ss")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @ApiModelProperty("结束时间，格式：HH:mm:ss")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    @ApiModelProperty("模式ID")
    private Long modeId;
}