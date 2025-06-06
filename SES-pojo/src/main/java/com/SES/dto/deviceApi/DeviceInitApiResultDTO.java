package com.SES.dto.deviceApi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 设备初始化Api的返回结果
 */
@Data
@ApiModel(description = "设备初始化Api的返回结果")
public class DeviceInitApiResultDTO implements Serializable {

    @ApiModelProperty("设备默认运行模式名")
    private String defaultModeName;

    @ApiModelProperty("设备运行模式名列表")
    private List<String> modeList ;
}