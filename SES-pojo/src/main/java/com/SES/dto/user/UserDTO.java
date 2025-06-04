package com.SES.dto.user;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册时传递的数据模型
 */
@Data
@ApiModel(description = "用户注册时传递的数据模型")
public class UserDTO implements Serializable {

    private String username;

    private String password;
}