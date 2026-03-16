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
                        "/api/v1/collections/**",      // 收藏相关接口
                        "/api/v1/admin/**",             // 后台管理接口
                        "/api/v1/auth/me"
                )
                .excludePathPatterns(
                        "/api/v1/auth/login",
                        "/api/v1/auth/register",
                        "/api/v1/auth/code",
                        "/api/v1/categories",
                        "/api/v1/tags",
                        "/api/v1/comments/article/**",
                        "/api/v1/collections/public/**",
                        "/api/v1/articles",
                        "/api/v1/users/profile/**"
                );
    }
}