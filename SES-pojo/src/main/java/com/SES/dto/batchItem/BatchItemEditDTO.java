package com.SES.dto.batchItem;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改批量操作条目时传递的数据模型
 */
@Data
@ApiModel(description = "修改批量操作条目时传递的数据模型")
public class BatchItemEditDTO implements Serializable {

    @ApiModelProperty("设备ID")
    private Long deviceId;

    @ApiModelProperty("是否应用策略（0=解绑策略, 1=应用policyId, NULL=不修改）")
    private Integer isApplyPolicy;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("模式ID")
    private Long modeId;

    @ApiModelProperty("策略ID（可为空）")
    private Long policyId;
}