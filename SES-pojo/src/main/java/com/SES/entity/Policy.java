package com.SES.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Policy implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long deviceId;

    private String name;

    private LocalDateTime createtime;

    private LocalDateTime updatetime;
}