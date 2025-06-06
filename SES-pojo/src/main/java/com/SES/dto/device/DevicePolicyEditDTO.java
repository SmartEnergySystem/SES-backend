package com.SES.dto.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改设备策略时传递的数据模型
 */
@Data
@ApiModel(description = "修改设备策略时传递的数据模型")
public class DevicePolicyEditDTO implements Serializable {

    @ApiModelProperty("是否应用策略：0=解绑策略，1=应用策略")
    private Integer isApplyPolicy;

    @ApiModelProperty("策略ID")
    private Long policyId;
}
