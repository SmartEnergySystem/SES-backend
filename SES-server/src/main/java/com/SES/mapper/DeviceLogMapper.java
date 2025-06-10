package com.SES.mapper;

import com.SES.dto.log.DeviceLogDTO;
import com.SES.dto.log.DeviceLogDataDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DeviceLogMapper {

    /**
     * 插入设备日志
     * @param dto
     */
    @Insert("INSERT INTO device_log " +
            "(user_id, user_username, device_id, device_name, start_time, end_time, status, mode_name, policy_name, policy, power, energy_consumption) " +
            "VALUES " +
            "(#{userId}, #{username}, #{deviceId}, #{deviceName}, #{startTime}, #{endTime}, #{status}, #{modeName}, #{policyName}, #{policy}, #{power}, #{energyConsumption})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(DeviceLogDTO dto);

    /**
     * 获得最新一条日志的设备数据部分
     * @param deviceId
     * @return
     */
    DeviceLogDataDTO getLatestDataByDeviceId(Long deviceId);

    /**
     * 根据设备ID和时间段查询日志列表
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志数据列表
     */
    List<DeviceLogDataDTO> getLogsByDeviceIdAndTimeRange(Long deviceId, LocalDateTime startTime, LocalDateTime endTime);

}