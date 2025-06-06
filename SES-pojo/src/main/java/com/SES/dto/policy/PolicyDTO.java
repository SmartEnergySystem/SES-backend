package com.SES.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 新增策略时传递的数据模型
 */
@Data
@ApiModel(description = "新增策略时传递的数据模型")
public class PolicyDTO implements Serializable {

    @ApiModelProperty("策略名称")
    private String name;

    @ApiModelProperty("设备ID")
    private Long deviceId;
}
