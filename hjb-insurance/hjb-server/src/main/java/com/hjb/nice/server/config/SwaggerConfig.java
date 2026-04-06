package com.hjb.nice.server.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String BEARER_AUTH = "Bearer Authentication";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HJB Insurance API")
                        .description("HJB 保险系统接口文档\n\n" +
                                "**使用说明：**\n" +
                                "1. 调用 `/api/auth/employee/login` 或 `/api/auth/customer/login` 获取 Token\n" +
                                "2. 点击右上角 **Authorize** 按钮，输入 Token 即可调用受保护接口")
                        .version("1.0.0"))
                // 全局添加 Bearer Token 认证
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH,
                                new SecurityScheme()
                                        .name(BEARER_AUTH)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
