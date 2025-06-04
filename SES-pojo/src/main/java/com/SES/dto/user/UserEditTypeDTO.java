package com.SES.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录
 */
@Data
@ApiModel(description = "用户登录时传递的数据模型")
public class UserEditTypeDTO implements Serializable {

    private Integer type;

}