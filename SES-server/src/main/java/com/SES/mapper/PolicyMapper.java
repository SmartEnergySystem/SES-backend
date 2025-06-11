package com.SES.mapper;

import com.SES.entity.Policy;
import org.apache.ibatis.annotations.*;

import java.time.LocalTime;
import java.util.List;

@Mapper
public interface PolicyMapper {

    /**
     * 插入策略
     * @param policy
     */
    @Insert("INSERT INTO policy (device_id, name, createtime, updatetime) " +
            "VALUES (#{deviceId}, #{name}, #{createtime}, #{updatetime})")
    void insert(Policy policy);

    /**
     * 根据策略ID查询策略
     * @param id
     * @return
     */
    @Select("SELECT * FROM policy WHERE id = #{id}")
    Policy getById(Long id);

    /**
     * 根据设备ID查询策略列表
     * @param deviceId
     * @return
     */
    @Select("SELECT * FROM policy WHERE device_id = #{deviceId} ORDER BY createtime DESC")
    List<Policy> getByDeviceId(Long deviceId);

    /**
     * 删除策略
     * @param id
     */
    @Delete("DELETE FROM policy WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * 根据设备ID删除所有策略
     * @param deviceId
     */
    @Delete("DELETE FROM policy WHERE device_id = #{deviceId}")
    void deleteByDeviceId(Long deviceId);

    /**
     * 动态更新策略
     * @param policy
     */
    void update(Policy policy);


}
