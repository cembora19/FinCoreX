package com.fincorex.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI finCoreXOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FinCoreX API")
                        .version("v1")
                        .description("Mini financial core and trading platform API"));
    }
}
