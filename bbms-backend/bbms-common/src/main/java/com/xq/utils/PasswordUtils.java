package com.xq.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码工具类（SHA-256 + 盐值加密）
 */
public class PasswordUtils {

    private static final int SALT_LENGTH = 16;
    private static final String ALGORITHM = "SHA-256";

    /**
     * 生成盐值
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 加密密码（带盐值）
     */
    public static String encode(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 加密密码（自动生成盐值）
     */
    public static String encode(String password) {
        String salt = generateSalt();
        return salt + ":" + encode(password, salt);
    }

    /**
     * 验证密码
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        try {
            // 解析盐值和加密后的密码
            String[] parts = encodedPassword.split(":");
            if (parts.length != 2) {
                return false;
            }
            String salt = parts[0];
            String storedHash = parts[1];

            // 计算输入密码的哈希值
            String computedHash = encode(rawPassword, salt);
            return storedHash.equals(computedHash);
        } catch (Exception e) {
            return false;
        }
    }
}