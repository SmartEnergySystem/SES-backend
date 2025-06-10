package com.SES.vo.deviceData;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDataVO {
    private Long deviceId;
    private Integer status;
    private String modeName;
    private Float power;
    private String policyName;

    private Integer isRealTime;          // 1 表示是实时数据，0 表示非实时数据

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdatedTime;       // 最后更新时间
}
