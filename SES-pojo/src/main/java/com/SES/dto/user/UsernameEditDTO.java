package com.SES.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UsernameEditDTO implements Serializable {

    //新密码
    private String newUsername;

}
