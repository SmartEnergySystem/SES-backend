package com.SES.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 模拟设备模式使用的常量
 */
public class SimDeviceModeConstant {


    // 故障类型常量（用于本项目测试）
    public static final String FAULT_SHORT_CIRCUIT = "故障（短路）";
    public static final String FAULT_DEVICE_FAILURE = "故障（设备故障）";

    // 故障类型列表
    public static final List<String> FAULT_LIST = Arrays.asList(
            FAULT_SHORT_CIRCUIT,
            FAULT_DEVICE_FAILURE
    );
}
