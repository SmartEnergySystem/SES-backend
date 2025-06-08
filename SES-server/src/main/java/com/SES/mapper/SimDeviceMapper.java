package com.SES.mapper;

import com.SES.entity.SimDevice;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SimDeviceMapper {
    /**
     * 插入模拟设备
     * @param simDevice
     */
    @Insert("INSERT INTO sim_device (device_id, status, mode_name) " +
            "VALUES (#{deviceId}, #{status}, #{modeName})")
    void insert(SimDevice simDevice);


    /**
     * 更新模拟设备
     * @param simDevice
     */
    void update(SimDevice simDevice);


    /**
     * 根据设备id查询模拟设备
     * @param deviceId
     * @return
     */
    @Select("SELECT * FROM sim_device WHERE device_id = #{deviceId}")
    SimDevice getByDeviceId(Long deviceId);

    /**
     * 根据设备id删除模拟设备
     * @param deviceId
     */
    @Delete("DELETE FROM sim_device WHERE device_id = #{deviceId}")
    void deleteByDeviceId(Long deviceId);
}
