package com.SES.dto.deviceData;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "设备当前数据查询时传递的数据模型")
public class DeviceDataQueryDTO {

    @ApiModelProperty("要查询的设备id列表")
    List<Long> idList;
}
