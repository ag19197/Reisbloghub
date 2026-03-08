package com.Reisblog.utils;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

// IP工具类
/*
*  是一个纯静态工具类，所有方法都是 public static
*  它不需要依赖 Spring 容器中的任何 Bean
* */
public class IpUtils {
    /**
     * 获取客户端真实IP地址（考虑代理）
     * @param request HttpServletRequest
     * @return 客户端IP
     */
    public static String getClientIp(HttpServletRequest request) {
        // 1. 尝试从常见的代理头获取
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For 可能包含多个IP，取第一个（客户端真实IP）
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("WL-Proxy-Client-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("HTTP_CLIENT_IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        // 2. 如果以上都没有，直接获取远程地址
        ip = request.getRemoteAddr();

        // 3. 处理本地IPv6地址
        if ("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }
}
