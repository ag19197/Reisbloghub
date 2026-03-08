package com.Reisblog.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Swagger 配置
@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ReisBlogHub API")
                        .description("个人博客系统接口文档")
                        .version("1.0.0")
                        .contact(new Contact().name("Rei").email("1229550645@qq.com")));
    }
}
