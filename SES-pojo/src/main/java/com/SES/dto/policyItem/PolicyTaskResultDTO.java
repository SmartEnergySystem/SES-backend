package com.SES.dto.policyItem;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * 根据策略查询控制任务
 */
@Data
@ApiModel(description = "根据策略查询控制任务时得到的数据模型")
public class PolicyTaskResultDTO implements Serializable {
    private Integer status;
    private String modeName;
}