package com.SES.dto.deviceData;

import com.SES.vo.deviceData.DeviceReportVO;
import lombok.Data;

import java.util.List;

@Data
public class DeviceReportResultDTO {
    private long total; //总记录数
    private Float totalEnergyConsumption; //总用电量
    private List<DeviceReportVO> deviceReports;

}
