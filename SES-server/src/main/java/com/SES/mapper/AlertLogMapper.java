package com.SES.mapper;

import com.SES.dto.log.AlertLogDTO;
import com.SES.dto.log.AlertLogDataDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AlertLogMapper {

    /**
     * 插入警报日志
     * @param dto 包含插入数据的对象
     */
    @Insert("INSERT INTO alert_log " +
            "(user_id, user_username, device_id, device_name, time, level, status, mode_name, policy_name, policy, message) " +
            "VALUES " +
            "(#{userId}, #{username}, #{deviceId}, #{deviceName}, #{time}, #{level}, #{status}, #{modeName}, #{policyName}, #{policy}, #{message})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(AlertLogDTO dto);

    /**
     * 根据设备ID和时间段查询警报日志列表
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志数据列表
     */
    List<AlertLogDataDTO> getLogsByDeviceIdAndTimeRange(Long deviceId, LocalDateTime startTime, LocalDateTime endTime);
}