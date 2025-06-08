package com.SES.dto.batch;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "新增批量操作时传递的数据模型")
public class BatchAddDTO implements Serializable {

    @ApiModelProperty(value = "批量操作名称", required = true)
    private String name;
}