package com.SES.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long policyId;

    private LocalTime startTime;

    private LocalTime endTime;

    private Long modeId;
}