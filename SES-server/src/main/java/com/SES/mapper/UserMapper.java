package com.SES.mapper;

import com.SES.annotation.AutoFill;
import com.SES.dto.user.UserPageQueryDTO;
import com.SES.entity.User;
import com.SES.enumeration.OperationType;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User getByUsername(String username);

    /**
     * 根据用户ID查询用户
     * @param id 用户ID
     * @return 用户对象
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User getById(Long id);

    /**
     * 插入用户
     * @param user 用户对象
     */
    @Insert("insert into user (username, password, status , type,createtime, updatetime, create_user, update_user) " +
            "values " +
            "(#{username},#{password},#{status},#{type},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @AutoFill(OperationType.INSERT)
    void insert(User user);

    /**
     * 根据主键动态修改属性
     * @param user
     */
    @AutoFill(OperationType.UPDATE)
    void update(User user);

    /**
     * 用户分页查询
     * @param userPageQueryDTO
     * @return
     */
    Page<User> pageQueny(UserPageQueryDTO userPageQueryDTO);
}