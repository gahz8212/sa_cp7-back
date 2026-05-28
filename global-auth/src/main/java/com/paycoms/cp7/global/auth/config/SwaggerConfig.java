package com.paycoms.cp7.global.auth.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.paycoms.cp7.global.auth.annotation.LoginUser;

@Configuration
public class SwaggerConfig {
    static {
        // LoginUser 어노테이션이 붙는 객체 타입(예: UserDto)을 설정
        // 이 타입은 Swagger 문서 생성 시 무시됩니다.
        SpringDocUtils.getConfig().addAnnotationsToIgnore(LoginUser.class);
    }

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "jwtAuth";
        // API 요청 시 JWT 인증을 위한 설정
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .info(new Info()
                        .title("CP7 API 명세서")
                        .description("CP7 프로젝트의 백엔드 API 문서입니다.")
                        .version("v0.0.1"))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}