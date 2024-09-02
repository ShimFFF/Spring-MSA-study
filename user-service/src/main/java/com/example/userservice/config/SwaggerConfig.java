package com.example.userservice.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

//        SecurityScheme apiKey = new SecurityScheme()
//                .type(SecurityScheme.Type.HTTP)
//                .in(SecurityScheme.In.HEADER)
//                .name("Authorization")
//                .scheme("Bearer")
//                .bearerFormat("JWT");

//        SecurityRequirement securityRequirement = new SecurityRequirement()
//                .addList("Bearer Token");

        return new OpenAPI()
                .components(new Components())
                //.components(new Components().addSecuritySchemes("Bearer Token", apiKey))
                //.addSecurityItem(securityRequirement)
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("MAS UserService API")
                .description("MAS 실습을 위한 UserService API")
                .version("1.0.0");
    }

}
