package com.SES.interceptor;

import com.SES.annotation.PassToken;
import com.SES.constant.JwtClaimsConstant;
import com.SES.context.BaseContext;
import com.SES.properties.JwtProperties;
import com.SES.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 获取类、方法上的 @PassToken 注解
        boolean classAnnotation = handlerMethod.getBeanType().isAnnotationPresent(PassToken.class);
        boolean methodAnnotation = handlerMethod.getMethod().isAnnotationPresent(PassToken.class);

        // 如果类上或方法上有 @PassToken，直接放行
        if (classAnnotation || methodAnnotation) {
            return true;
        }

        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getAdminTokenName());

        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);

            //从 Claims 中提取用户 ID,存储到线程上下文 BaseContext 中，供后续业务逻辑使用.
            Long empId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("当前用户id：{}", empId);
            BaseContext.setCurrentId(empId);
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }
}
