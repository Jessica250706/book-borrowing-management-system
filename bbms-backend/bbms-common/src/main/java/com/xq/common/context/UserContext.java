package com.xq.common.context;

/**
 * 用户上下文，用于在同一个线程中共享用户信息
 */
public class UserContext {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_ROLE = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<Long> ROLE_ID = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> IS_ADMIN = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static void setUserRole(String userRole) {
        USER_ROLE.set(userRole);
    }

    public static String getUserRole() {
        return USER_ROLE.get();
    }

    public static void setUsername(String username) {
        USERNAME.set(username);
    }

    public static String getUsername() {
        return USERNAME.get();
    }

    public static void setRoleId(Long roleId) {
        ROLE_ID.set(roleId);
    }

    public static Long getRoleId() {
        return ROLE_ID.get();
    }

    public static void setIsAdmin(Boolean isAdmin) {
        IS_ADMIN.set(isAdmin);
    }

    public static Boolean getIsAdmin() {
        return IS_ADMIN.get() != null ? IS_ADMIN.get() : false;
    }

    public static void clear() {
        USER_ID.remove();
        USER_ROLE.remove();
        USERNAME.remove();
        ROLE_ID.remove();
        IS_ADMIN.remove();
    }

    /**
     * 设置完整的用户信息
     */
    public static void setUserInfo(Long userId, String username, Long roleId, String roleCode) {
        setUserId(userId);
        setUsername(username);
        setRoleId(roleId);
        setUserRole(roleCode);
        setIsAdmin("ADMIN".equals(roleCode) || "SYS_ADMIN".equals(roleCode));
    }
}