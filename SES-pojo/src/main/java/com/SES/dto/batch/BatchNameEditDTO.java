package com.SES.dto.batch;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "修改批量操作名称时传递的数据模型")
public class BatchNameEditDTO implements Serializable {

    @ApiModelProperty(value = "新的批量操作名称", required = true)
    private String name;
}