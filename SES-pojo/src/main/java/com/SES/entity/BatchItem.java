package com.SES.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long batchId;

    private Long deviceId;

    private Integer isApplyPolicy; // 0=解绑策略, 1=应用policyId, NULL=不修改

    private Integer status;

    private Long modeId;

    private Long policyId; // 可为空
}