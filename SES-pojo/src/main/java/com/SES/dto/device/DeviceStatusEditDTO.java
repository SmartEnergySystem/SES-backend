package com.SES.dto.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 控制设备运行状态时传递的数据模型
 */
@Data
@ApiModel(description = "控制设备运行状态时传递的数据模型")
public class DeviceStatusEditDTO implements Serializable {

    @ApiModelProperty("设备运行状态")
    private Integer status;
}
