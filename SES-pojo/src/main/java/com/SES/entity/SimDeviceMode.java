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
public class SimDeviceMode implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long deviceId;

    private String name;

    private Integer power; // 单位：W
}