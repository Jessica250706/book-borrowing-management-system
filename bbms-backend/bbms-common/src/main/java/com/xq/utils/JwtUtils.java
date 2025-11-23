package com.xq.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Component
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtUtils {

    //颁发者
    private String issuer;
    //秘钥
    private String secret;
    //过期时间（分钟）
    private int expiration;

    /**
     * 生成token
     */
    public String generateToken(Map<String,String> map){
        //设置令牌的过期时间
        Calendar instance = Calendar.getInstance();
        //设置失效时间
        instance.add(Calendar.MINUTE,expiration);
        //创建JWT Builder
        JWTCreator.Builder builder = JWT.create();
        //设置载荷(payload)
        map.forEach((k,v)->{
            builder.withClaim(k,v);
        });
        //生成token
        String token = builder.withIssuer(issuer)
                .withIssuedAt(new Date())
                .withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(secret));
        return token;
    }

    /**
     * 生成用户登录token
     */
    public String generateUserToken(Long userId, String username, Long roleId, String roleCode) {
        Map<String, String> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("username", username);
        claims.put("roleId", roleId.toString());
        claims.put("roleCode", roleCode);
        claims.put("isAdmin", String.valueOf(isAdminRole(roleCode)));

        return generateToken(claims);
    }

    /**
     * 验证令牌是否合法
     */
    public boolean verify(String token){
        try {
            JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
        } catch (JWTVerificationException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    /**
     * 解析token
     */
    public DecodedJWT jwtDecode(String token){
        try {
            return JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("token签名错误!");
        } catch (AlgorithmMismatchException e) {
            throw new RuntimeException("token算法不匹配!");
        } catch (TokenExpiredException e) {
            throw new RuntimeException("token过期!");
        } catch (Exception e) {
            throw new RuntimeException("token解析失败!");
        }
    }

    /**
     * 安全解析token（宽松模式，即使过期也返回DecodedJWT）
     */
    public DecodedJWT safeJwtDecode(String token) {
        try {
            // 使用不验证过期时间的方式解析
            return JWT.decode(token);
        } catch (Exception e) {
            throw new RuntimeException("token解析失败: " + e.getMessage());
        }
    }

    /**
     * 从token中获取用户ID
     */
    public Long getUserId(String token) {
        DecodedJWT decodedJWT = jwtDecode(token);
        return Long.valueOf(decodedJWT.getClaim("userId").asString());
    }

    /**
     * 安全获取用户ID（即使token过期也能获取）
     */
    public Long safeGetUserId(String token) {
        try {
            DecodedJWT decodedJWT = safeJwtDecode(token);
            return Long.valueOf(decodedJWT.getClaim("userId").asString());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从token中获取用户名
     */
    public String getUsername(String token) {
        DecodedJWT decodedJWT = jwtDecode(token);
        return decodedJWT.getClaim("username").asString();
    }

    /**
     * 安全获取用户名（即使token过期也能获取）
     */
    public String safeGetUsername(String token) {
        try {
            DecodedJWT decodedJWT = safeJwtDecode(token);
            return decodedJWT.getClaim("username").asString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从token中获取角色ID
     */
    public Long getRoleId(String token) {
        DecodedJWT decodedJWT = jwtDecode(token);
        return Long.valueOf(decodedJWT.getClaim("roleId").asString());
    }

    /**
     * 安全获取角色ID（即使token过期也能获取）
     */
    public Long safeGetRoleId(String token) {
        try {
            DecodedJWT decodedJWT = safeJwtDecode(token);
            return Long.valueOf(decodedJWT.getClaim("roleId").asString());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从token中获取角色编码
     */
    public String getRoleCode(String token) {
        DecodedJWT decodedJWT = jwtDecode(token);
        return decodedJWT.getClaim("roleCode").asString();
    }

    /**
     * 安全获取角色编码（即使token过期也能获取）
     */
    public String safeGetRoleCode(String token) {
        try {
            DecodedJWT decodedJWT = safeJwtDecode(token);
            return decodedJWT.getClaim("roleCode").asString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从token中判断是否是管理员
     */
    public Boolean isAdmin(String token) {
        DecodedJWT decodedJWT = jwtDecode(token);
        return Boolean.valueOf(decodedJWT.getClaim("isAdmin").asString());
    }

    /**
     * 判断是否是管理员角色
     */
    private boolean isAdminRole(String roleCode) {
        return "ADMIN".equals(roleCode) || "SYS_ADMIN".equals(roleCode);
    }

    /**
     * 验证token是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            DecodedJWT decodedJWT = jwtDecode(token);
            return decodedJWT.getExpiresAt().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 安全验证token是否过期
     */
    public boolean safeIsTokenExpired(String token) {
        try {
            DecodedJWT decodedJWT = safeJwtDecode(token);
            return decodedJWT.getExpiresAt().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 检查token是否即将过期（在指定分钟内）
     */
    public boolean isTokenExpiringSoon(String token, int minutes) {
        try {
            DecodedJWT decodedJWT = safeJwtDecode(token);
            Date expiresAt = decodedJWT.getExpiresAt();
            Date now = new Date();

            // 计算剩余时间（毫秒）
            long remainingTime = expiresAt.getTime() - now.getTime();
            long threshold = minutes * 60 * 1000L; // 转换为毫秒

            return remainingTime <= threshold && remainingTime > 0;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 获取token剩余有效时间（分钟）
     */
    public long getTokenRemainingMinutes(String token) {
        try {
            DecodedJWT decodedJWT = safeJwtDecode(token);
            Date expiresAt = decodedJWT.getExpiresAt();
            Date now = new Date();

            long remainingTime = expiresAt.getTime() - now.getTime();
            return Math.max(0, remainingTime / (60 * 1000)); // 转换为分钟，确保非负数
        } catch (Exception e) {
            return 0;
        }
    }
}