package com.Reisblog.utils;

import javax.servlet.http.HttpServletRequest;

public class AdminAuthUtil {
    public static Long getAdminId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("userRole");
        if (!"ADMIN".equals(role)) {
            throw new RuntimeException("无管理员权限");
        }
        return userId;
    }
}