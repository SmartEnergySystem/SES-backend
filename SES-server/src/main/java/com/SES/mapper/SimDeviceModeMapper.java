package com.SES.mapper;

import com.SES.entity.SimDeviceMode;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SimDeviceModeMapper {

    /**
     * 插入模拟设备模式
     * @param simDeviceMode
     */
    @Insert("INSERT INTO sim_device_mode (device_id, name, power) " +
            "VALUES (#{deviceId}, #{name}, #{power})")
    void insert(SimDeviceMode simDeviceMode);

    /**
     * 更新模拟设备模式
     * @param simDeviceMode
     */
    void update(SimDeviceMode simDeviceMode);

    /**
     * 根据设备ID查询所有对应的模拟设备模式
     * @param deviceId 设备ID
     * @return 模式列表
     */
    @Select("SELECT * FROM sim_device_mode WHERE device_id = #{deviceId}")
    List<SimDeviceMode> getByDeviceId(String deviceId);

    /**
     * 根据设备ID和模式名称查询特定模式
     * @param deviceId 设备ID
     * @param modeName 模式名称
     * @return 匹配的模拟设备模式
     */
    @Select("SELECT * FROM sim_device_mode WHERE device_id = #{deviceId} AND name = #{modeName}")
    SimDeviceMode getByDeviceIdAndModeName(Long deviceId, String modeName);

}
