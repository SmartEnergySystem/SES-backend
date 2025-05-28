package com.SES.service;

import com.SES.dto.UserLoginDTO;
import com.SES.entity.User;

public interface UserService {

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    User login(UserLoginDTO userLoginDTO);


}
