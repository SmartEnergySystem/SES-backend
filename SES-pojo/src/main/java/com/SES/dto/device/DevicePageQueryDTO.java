package com.SES.dto.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 设备分页查询时传递的数据模型
 */
@Data
@ApiModel(description = "设备分页查询时传递的数据模型")
public class DevicePageQueryDTO implements Serializable {

    @ApiModelProperty("页码")
    private int page;

    @ApiModelProperty("每页记录数")
    private int pageSize;

    @ApiModelProperty("设备名称")
    private String name;

    @ApiModelProperty("用户ID（内部使用）")
    private Long userId;
}
