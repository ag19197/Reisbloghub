package com.Reisblog.interceptor;

import com.Reisblog.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// JWT 拦截器
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果是 OPTIONS 请求，直接放行（不验证 token）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String uri = request.getRequestURI();
        System.out.println("========== JwtInterceptor ==========");
        System.out.println("请求路径: " + uri);
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization头: " + authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 没有 token，返回 401 错误
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录\",\"data\":null}");
            return false;
        }
        String token = authHeader.substring(7);
        if (!jwtUtils.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"token无效\",\"data\":null}");
            return false;
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        String role = jwtUtils.getRoleFromToken(token);
        System.out.println("解析 userId: " + userId + ", role: " + role);
        request.setAttribute("userId", userId);
        request.setAttribute("userRole", role);
        return true;
    }
}