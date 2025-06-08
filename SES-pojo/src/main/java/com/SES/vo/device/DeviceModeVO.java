package com.SES.vo.device;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "设备当前状态信息")
public class DeviceModeVO implements Serializable {
    private Long id;
    private String name;

}
