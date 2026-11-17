package com.platform.util;

import org.springframework.security.core.Authentication;

/**
 * 获取当前登录用户 ID 的工具类
 */
public class AuthUtil {
    public static Long getCurrentUserId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        return null;
    }
}
