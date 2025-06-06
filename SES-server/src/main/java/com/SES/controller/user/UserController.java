package com.SES.controller.user;

import com.SES.annotation.PassToken;
import com.SES.constant.JwtClaimsConstant;
import com.SES.constant.MessageConstant;
import com.SES.dto.user.*;
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

import static com.SES.utils.JwtUtil.extractUserIdAndExpFromToken;

/**
 * 用户理
 */
@RestController
@RequestMapping("/api/user")
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
    @PassToken
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO);

        User user = userService.login(userLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId()); // 向claims中传入用户id
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        // 创建返回对象
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId()) // 传递回去的当前用户id
                .username(user.getUsername())
                .token(token) // 传递回去的jwt令牌
                .build();

        return Result.success(userLoginVO);
    }


    /**
     * 新用户注册
     * @param userDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value="新用户注册")
    @PassToken
    public Result<String> register(@RequestBody UserDTO userDTO) {

        userService.register(userDTO);
        return Result.success();
    }

    /**
     * 修改密码
     * @param passwordEditDTO
     * @return
     */
    @PutMapping("/editPassword")
    @ApiOperation(value="修改密码")
    public Result<String> editPassword(@RequestBody PasswordEditDTO passwordEditDTO) {

        userService.editPassword(passwordEditDTO);
        return Result.success();
    }



    /**
     * 修改账号权限
     * @param id
     * @param userEditTypeDTO
     * @return
     */
    @PutMapping("/{id}/type")
    @ApiOperation(value="修改权限")
    public Result<String> editType(@PathVariable Long id,@RequestBody UserEditTypeDTO userEditTypeDTO) {
        Integer type = userEditTypeDTO.getType();
        log.info("修改账号权限：{},{}", id,type);
        userService.editType(id,type);
        return Result.success();
    }



    /**
     * 用户分页查询
     * @param userPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("用户分页查询")
    public Result<PageResult> page(UserPageQueryDTO userPageQueryDTO) {
        log.info("用户分页查询：{}", userPageQueryDTO);
        PageResult pageResult =  userService.pageQueny(userPageQueryDTO);

        return Result.success(pageResult);
    }


    /**
     * 启用禁用账号
     * @return
     */
    @PostMapping("/{id}/status/{status}")
    @ApiOperation(value="启用禁用账号")
    public Result<String> startOrStop(@PathVariable Long id,@PathVariable Integer status) {
        userService.startOrStop(id,status);
        return Result.success();
    }


    /**
     * 退出登录
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value="用户登出")
    public Result<String> logout() {
        return Result.success();
    }


    /**
     * 刷新 Token
     */
    @PostMapping("/refresh")
    @ApiOperation(value = "刷新 Token")
    @PassToken
    public Result<String> refresh(@RequestParam String oldToken) {
        try {
            // 调用提取方法
            Map<String, Long> claims = extractUserIdAndExpFromToken(oldToken);
            if (claims == null) {
                return Result.error("缺少必要字段或 token 格式错误");
            }

            Long userId = claims.get("user_id");
            Long exp = claims.get("exp");

            // 检查是否已过期
            long nowMillis = System.currentTimeMillis();
            long expMillis = exp * 1000L;

            if (expMillis > nowMillis) {
                return Result.error(MessageConstant.TOKEN_NOT_EXPIRED);
            }

            // 检查是否在容忍窗口内
            long expiredTimeAgo = nowMillis - expMillis;
            if (expiredTimeAgo > jwtProperties.getRefreshTolerance()) {
                return Result.error(MessageConstant.TOKEN_EXPIRED_TOO_LONG);
            }

            // 构建新 token 的 payload
            Map<String, Object> newClaims = new HashMap<>();
            newClaims.put(JwtClaimsConstant.USER_ID, userId);

            // 生成新 token
            String newToken = JwtUtil.createJWT(
                    jwtProperties.getAdminSecretKey(),
                    jwtProperties.getAdminTtl(),
                    newClaims);

            return Result.success(newToken);

        } catch (Exception e) {
            log.warn("无效的 token 或解析失败：{}", e.getMessage());
            return Result.error("无效的 token 或解析失败");
        }
    }


}
