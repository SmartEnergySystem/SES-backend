package com.SES.service;

import com.SES.dto.PasswordEditDTO;
import com.SES.dto.UserLoginDTO;
import com.SES.dto.UserDTO;
import com.SES.dto.UserPageQueryDTO;
import com.SES.entity.User;
import com.SES.result.PageResult;

public interface UserService {

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    User login(UserLoginDTO userLoginDTO);

    /**
     * 新用户注册
     * @param userDTO
     * @return
     */
    void register(UserDTO userDTO);

    /**
     * 修改密码
     * @param passwordEditDTO
     */
    void editPassword(PasswordEditDTO passwordEditDTO);

    /**
     * 修改账号权限
     * @param id
     * @param type
     */
    void editType(Long id, Integer type);


    /**
     * 判断当前操作用户是否为管理员
     */
    void checkCurrentUserIsAdmin();

    /**
     * 用户分页查询
     * @param userPageQueryDTO
     * @return
     */
    PageResult pageQueny(UserPageQueryDTO userPageQueryDTO);
}
