package com.SES.mapper;

import com.SES.dto.device.DevicePageQueryDTO;
import com.SES.entity.Device;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

@Mapper
public interface DeviceMapper {

    /**
     * 插入设备
     * @param device
     */
    @Insert("INSERT INTO device (user_id, name, last_known_status) " +
            "VALUES"+
            " (#{userId}, #{name}, #{lastKnownStatus})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Device device);

    /**
     * 根据设备ID查询设备
     * @param id
     * @return
     */
    @Select("SELECT * FROM device WHERE id = #{id}")
    Device getById(Long id);

    /**
     * 根据设备ID和用户ID查询设备
     * @param id
     * @param userId
     * @return
     */
    @Select("SELECT * FROM device WHERE id = #{id} AND user_id = #{userId}")
    Device getByIdAndUserId(Long id, Long userId);

    /**
     * 删除设备
     * @param id
     */
    @Delete("DELETE FROM device WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * 分页查询设备
     * @param devicePageQueryDTO
     * @return
     */
    Page<Device> pageQuery(DevicePageQueryDTO devicePageQueryDTO);

    /**
     * 动态更新设备
     * @param device
     */
    void update(Device device);
}
