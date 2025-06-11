package com.SES.dto.deviceData;

import com.SES.vo.deviceData.AlertReportVO;
import com.SES.vo.deviceData.DeviceReportVO;
import lombok.Data;

import java.util.List;

@Data
public class AlertReportResultDTO {
    private long total; //总记录数
    private List<AlertReportVO> alertReports;

}
