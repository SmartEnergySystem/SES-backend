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
public class SimDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long deviceId;

    private Integer status; // 0=关闭，1=开启，-1=异常或损坏

    private String modeName;
}