package com.SES.controller.admin;

import com.SES.constant.JwtClaimsConstant;
import com.SES.dto.UserLoginDTO;
import com.SES.entity.User;
import com.SES.properties.JwtProperties;
import com.SES.result.PageResult;
import com.SES.result.Result;
import com.SES.service.UserService;
import com.SES.utils.JwtUtil;
import com.SES.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户理
 */
@RestController
@RequestMapping("/admin/auth")
@Slf4j
@Api(tags="用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value="用户登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO);

        User user = userService.login(userLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .token(token) // 传递回去的jwt令牌
                .build();

        return Result.success(userLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value="用户退出")
    public Result<String> logout() {
        return Result.success();
    }






}
