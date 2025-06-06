package com.SES.dto.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改设备名称时传递的数据模型
 */
@Data
@ApiModel(description = "修改设备名称时传递的数据模型")
public class DeviceNameEditDTO implements Serializable {

    @ApiModelProperty("设备名称")
    private String name;
}
