package com.SES.mapper;

import com.SES.dto.log.DeviceLogDTO;
import com.SES.dto.log.DeviceLogDataDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

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
}