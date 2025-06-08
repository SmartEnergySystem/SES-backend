package com.SES.vo.user;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "用户登录信息")
public class UserLoginVO implements Serializable {

    private Long id;
    private String username;
    private String openid;
    private String token;

}
