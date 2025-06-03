package com.SES.service;

import com.SES.dto.UserLoginDTO;
import com.SES.dto.UserDTO;
import com.SES.entity.User;

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
}
