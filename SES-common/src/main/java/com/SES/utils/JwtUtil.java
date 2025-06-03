package com.SES.utils;

import com.SES.constant.JwtClaimsConstant;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * jwt工具类
 */
public class JwtUtil {
    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param secretKey jwt秘钥
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信息
     * @return
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 指定签名的时候使用的签名算法，也就是header那部分
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 生成JWT的时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        // 设置jwt的body
        JwtBuilder builder = Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
                // 设置过期时间
                .setExpiration(exp);

        return builder.compact();
    }

    /**
     * Token解密
     *
     * @param secretKey jwt秘钥 此秘钥一定要保留好在服务端, 不能暴露出去, 否则sign就可以被伪造, 如果对接多个客户端建议改造成多个
     * @param token     加密后的token
     * @return
     */
    public static Claims parseJWT(String secretKey, String token) {
        // 得到DefaultJwtParser
        Claims claims = Jwts.parser()
                // 设置签名的秘钥
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                // 设置需要解析的jwt
                .parseClaimsJws(token).getBody();
        return claims;
    }


    /**
     * 从旧 Token 中提取 user_id 和 exp
     * @param oldToken 原始的 JWT Token
     * @return
     */
    public static Map<String, Long> extractUserIdAndExpFromToken(String oldToken) {
        try {
            // Step 1: Base64 解码 JWT payload
            String[] parts = oldToken.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            String payloadB64 = parts[1];
            byte[] decodedBytes = Base64.getUrlDecoder().decode(payloadB64);
            String payloadJson = new String(decodedBytes, StandardCharsets.UTF_8);

            // Step 2: 提取字段
            String userIdKey = JwtClaimsConstant.USER_ID;
            Long userId = extractLongValue(payloadJson, userIdKey);
            Long exp = extractLongValue(payloadJson, "exp");

            if (userId == null || exp == null) {
                return null;
            }

            Map<String, Long> result = new HashMap<>();
            result.put("user_id", userId);
            result.put("exp", exp);
            return result;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 辅助方法：提取 JSON 字符串中的字段值
     * @param json
     * @param key
     * @return
     */
    public static Long extractLongValue(String json, String key) {
        // 匹配类似 "user_id":123 或 "exp":1717462792 的模式
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+)");
        java.util.regex.Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }

}
