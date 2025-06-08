package com.SES.dto.batch;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "批量操作分页查询参数")
public class BatchPageQueryDTO implements Serializable {

    @ApiModelProperty(value = "页码", required = true, example = "1")
    private Integer page;

    @ApiModelProperty(value = "每页数量", required = true, example = "10")
    private Integer pageSize;

    @ApiModelProperty(value = "批量操作名称(模糊查询)", required = false)
    private String name;

    private Long userId;

    private Integer offset;
}