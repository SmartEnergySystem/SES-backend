package com.SES.dto.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 新增设备时传递的数据模型
 */
@Data
@ApiModel(description = "新增设备时传递的数据模型")
public class DeviceDTO implements Serializable {

    @ApiModelProperty("设备名称")
    private String name;

    @ApiModelProperty("内置设备类型")
    private String type;
}
