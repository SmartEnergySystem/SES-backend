package com.SES.dto.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 控制设备模式时传递的数据模型
 */
@Data
@ApiModel(description = "控制设备模式时传递的数据模型")
public class DeviceModeEditDTO implements Serializable {

    @ApiModelProperty("该设备对应模式的ID")
    private Long modeId;
}
