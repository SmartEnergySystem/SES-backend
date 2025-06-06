package com.SES.dto.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 删除设备时传递的数据模型
 */
@Data
@ApiModel(description = "删除设备时传递的数据模型")
public class DeviceDeleteDTO implements Serializable {

    @ApiModelProperty("设备ID")
    private Long id;
}
