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
     * 生成token（兼容原有方法）
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
     * 生成用户登录token（新增方法）
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
     * 从token中获取用户ID（新增方法）
     */
    public Long getUserId(String token) {
        DecodedJWT decodedJWT = jwtDecode(token);
        return Long.valueOf(decodedJWT.getClaim("userId").asString());
    }

    /**
     * 从token中获取用户名（新增方法）
     */
    public String getUsername(String token) {
        DecodedJWT decodedJWT = jwtDecode(token);
        return decodedJWT.getClaim("username").asString();
    }

    /**
     * 从token中获取角色ID（新增方法）
     */
    public Long getRoleId(String token) {
        DecodedJWT decodedJWT = jwtDecode(token);
        return Long.valueOf(decodedJWT.getClaim("roleId").asString());
    }

    /**
     * 从token中获取角色编码（新增方法）
     */
    public String getRoleCode(String token) {
        DecodedJWT decodedJWT = jwtDecode(token);
        return decodedJWT.getClaim("roleCode").asString();
    }

    /**
     * 从token中判断是否是管理员（新增方法）
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
     * 验证token是否过期（新增方法）
     */
    public boolean isTokenExpired(String token) {
        try {
            DecodedJWT decodedJWT = jwtDecode(token);
            return decodedJWT.getExpiresAt().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}