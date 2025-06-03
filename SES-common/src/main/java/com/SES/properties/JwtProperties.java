package com.SES.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ses.jwt")
@Data
public class JwtProperties {

    /**
     * 用户端生成jwt令牌相关配置
     */
    private String adminSecretKey; // 密钥
    private long adminTtl; // 令牌有效期
    private String adminTokenName; //前端在请求头中传递 JWT 时使用的Header 名称
    private Long refreshTolerance; // 刷新 token 的容忍时间（毫秒）

    // 命名理论上应该以user开头，由于是魔改来的所以就叫admin了
    // 详细值见application.yml
}
