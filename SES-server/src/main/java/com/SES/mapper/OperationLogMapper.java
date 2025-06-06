package com.SES.mapper;

import com.SES.entity.OperationLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper {

    /**
     * 插入操作日志
     * @param operationLog
     */
    @Insert("INSERT INTO operation_log (user_id, user_username, device_id, device_name, time, " +
            "isApplyPolicy, status, mode_name, policy_name, policy, batch_name) " +
            "VALUES (#{userId}, #{userUsername}, #{deviceId}, #{deviceName}, #{time}, " +
            "#{isApplyPolicy}, #{status}, #{modeName}, #{policyName}, #{policy}, #{batchName})")
    void insert(OperationLog operationLog);
}
