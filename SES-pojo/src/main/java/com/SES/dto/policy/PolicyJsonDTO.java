package com.SES.dto;

import com.SES.dto.policyItem.PolicyItemDTO;
import com.SES.entity.PolicyItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 获取策略Json时使用的数据模型
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // 避免序列化 null 字段
@ApiModel(description = "获取策略Json时使用的数据模型")
public class PolicyJsonDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "策略ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "设备ID", example = "1001")
    private Long deviceId;

    @ApiModelProperty(value = "策略名称", example = "工作日自动模式")
    private String name;

    @ApiModelProperty(value = "创建时间", example = "2025-04-05T10:00:00")
    private LocalDateTime createtime;

    @ApiModelProperty(value = "最后更新时间", example = "2025-04-06T15:30:00")
    private LocalDateTime updatetime;

    @ApiModelProperty(value = "策略条目列表")
    private List<PolicyItem> items;
}