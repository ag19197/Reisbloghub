package com.Reisblog.config;

import com.Reisblog.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    // 添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns(
                        "/collections/**",      // 收藏相关接口
                        "/users/profile",        // 修改个人信息（如果有）
                        "/users/me",             // 获取当前用户信息（如果有）
                        "/api/v1/admin/**"   // 必须添加
                )
                .excludePathPatterns(
                        "/auth/**",              // 认证接口放行
                        "/users/profile/*",       // 获取公开个人主页（稍后说明）
                        "/users/*/collections/public", // 公开收藏列表放行
                        "/api/v1/collections/public/**",  // 公开收藏列表放行
                        "/api/v1/users/profile/**"  // 获取公开个人主页
                );
    }
}