package com.SES.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 策略视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "策略视图对象")
public class PolicyVO implements Serializable {

    @ApiModelProperty("策略ID")
    private Long id;

    @ApiModelProperty("设备ID")
    private Long deviceId;

    @ApiModelProperty("策略名称")
    private String name;

    @ApiModelProperty("创建时间")
    private LocalDateTime createtime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updatetime;

    @ApiModelProperty("策略条目数量")
    private Integer itemCount;
}
