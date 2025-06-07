package com.SES.dto.deviceApi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 设备查询Api的返回结果
 */
@Data
@ApiModel(description = "设备初始化Api的返回结果")
public class DeviceQueryApiResultDTO implements Serializable {

    @ApiModelProperty("当前运行状态")
    private Integer status;

    @ApiModelProperty("当前运行模式名")
    private String modeName;

    @ApiModelProperty("当前功率")
    private Float power;
}