package com.SES.dto.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 设备控制时传递的数据模型
 */
@Data
@ApiModel(description = "设备控制时传递的数据模型")
public class DeviceControlDTO implements Serializable {

    @ApiModelProperty("是否应用策略：0=解绑策略，1=应用策略，null=不操作")
    private Integer isApplyPolicy;

    @ApiModelProperty("策略ID")
    private Long policyId;

    @ApiModelProperty("设备运行状态")
    private Integer status;

    @ApiModelProperty("该设备对应模式的ID")
    private Long modeId;
}
