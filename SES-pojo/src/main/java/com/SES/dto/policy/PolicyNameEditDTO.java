package com.SES.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改策略名称时传递的数据模型
 */
@Data
@ApiModel(description = "修改策略名称时传递的数据模型")
public class PolicyNameEditDTO implements Serializable {

    @ApiModelProperty("策略名称")
    private String name;
}
