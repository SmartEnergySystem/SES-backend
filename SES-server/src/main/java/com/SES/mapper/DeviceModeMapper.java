package com.SES.mapper;

import com.SES.entity.DeviceMode;
import com.SES.vo.device.DeviceModeVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DeviceModeMapper {

    /**
     * 插入设备模式
     * @param deviceMode 设备模式对象
     */
    @Insert("INSERT INTO device_mode (device_id, name) " +
            "VALUES (#{deviceId}, #{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(DeviceMode deviceMode);

    /**
     * 更新设备模式（动态字段更新）
     * @param deviceMode 设备模式对象
     */
    void update(DeviceMode deviceMode);

    /**
     * 根据ID查询设备模式
     * @param id 设备ID
     * @return 模式列表
     */
    @Select("SELECT * FROM device_mode WHERE id = #{id}")
    DeviceMode getById(Long id);


    /**
     * 根据设备ID查询所有对应的设备模式
     * @param deviceId 设备ID
     * @return 模式列表
     */
    @Select("SELECT * FROM device_mode WHERE device_id = #{deviceId} ORDER BY id ASC")
    List<DeviceMode> getByDeviceId(Long deviceId);

    /**
     * 根据设备ID查询设备模式VO
     * @param deviceId 设备ID
     * @return 设备模式VO列表
     */
    @Select("SELECT id, name FROM device_mode WHERE device_id = #{deviceId} ORDER BY id ASC")
    List<DeviceModeVO> getVOByDeviceId(Long deviceId);

    /**
     * 根据设备ID和模式名称查询特定模式
     * @param deviceId 设备ID
     * @param modeName 模式名称
     * @return 匹配的设备模式
     */
    @Select("SELECT * FROM device_mode WHERE device_id = #{deviceId} AND name = #{modeName}")
    DeviceMode getByDeviceIdAndModeName(Long deviceId, String modeName);
}