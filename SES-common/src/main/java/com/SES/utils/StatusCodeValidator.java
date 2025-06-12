package com.SES.utils;

import com.SES.constant.DeviceStatusConstant;
import com.SES.constant.StatusConstant;
import com.SES.constant.UserTypeConstant;
import com.SES.exception.BaseException;

/**
 * 状态码验证工具类
 * 用于验证前端输入的特殊状态码是否在规定范围内
 */
public class StatusCodeValidator {

    /**
     * 验证设备状态码
     * @param status 设备状态码
     * @throws BaseException 如果状态码无效
     */
    public static void validateDeviceStatus(Integer status) {
        if (status == null) {
            return; // null值允许，表示不修改状态
        }
        
        if (!isValidDeviceStatus(status)) {
            throw new BaseException("无效的设备状态码：" + status + "，有效值为：0(关机)、1(开机)、-1(故障)");
        }
    }

    /**
     * 验证策略应用状态码
     * @param isApplyPolicy 策略应用状态码
     * @throws BaseException 如果状态码无效
     */
    public static void validatePolicyApplyStatus(Integer isApplyPolicy) {
        if (isApplyPolicy == null) {
            return; // null值允许，表示不操作策略
        }
        
        if (!isValidPolicyApplyStatus(isApplyPolicy)) {
            throw new BaseException("无效的策略应用状态码：" + isApplyPolicy + "，有效值为：0(解绑策略)、1(应用策略)");
        }
    }

    /**
     * 验证启用/禁用状态码
     * @param status 启用/禁用状态码
     * @throws BaseException 如果状态码无效
     */
    public static void validateEnableDisableStatus(Integer status) {
        if (status == null) {
            return; // null值允许
        }
        
        if (!isValidEnableDisableStatus(status)) {
            throw new BaseException("无效的启用/禁用状态码：" + status + "，有效值为：0(禁用)、1(启用)");
        }
    }

    /**
     * 验证用户类型状态码
     * @param type 用户类型状态码
     * @throws BaseException 如果状态码无效
     */
    public static void validateUserType(Integer type) {
        if (type == null) {
            return; // null值允许
        }
        
        if (!isValidUserType(type)) {
            throw new BaseException("无效的用户类型状态码：" + type + "，有效值为：0(普通用户)、1(管理员)");
        }
    }

    /**
     * 检查设备状态码是否有效
     * @param status 设备状态码
     * @return 是否有效
     */
    private static boolean isValidDeviceStatus(Integer status) {
        return status.equals(DeviceStatusConstant.OFF) || 
               status.equals(DeviceStatusConstant.ON) || 
               status.equals(DeviceStatusConstant.FAULT);
    }

    /**
     * 检查策略应用状态码是否有效
     * @param isApplyPolicy 策略应用状态码
     * @return 是否有效
     */
    private static boolean isValidPolicyApplyStatus(Integer isApplyPolicy) {
        return isApplyPolicy.equals(0) || isApplyPolicy.equals(1);
    }

    /**
     * 检查启用/禁用状态码是否有效
     * @param status 启用/禁用状态码
     * @return 是否有效
     */
    private static boolean isValidEnableDisableStatus(Integer status) {
        return status.equals(StatusConstant.DISABLE) || status.equals(StatusConstant.ENABLE);
    }

    /**
     * 检查用户类型状态码是否有效
     * @param type 用户类型状态码
     * @return 是否有效
     */
    private static boolean isValidUserType(Integer type) {
        return type.equals(UserTypeConstant.NORMAL) || type.equals(UserTypeConstant.ADMIN);
    }
}
