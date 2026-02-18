package com.poc.apistyles.adapter.rest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API Styles PoC - REST API")
                .description("REST API for the API Styles Proof of Concept")
                .version("1.0.0")
                .contact(new Contact().name("API Team")));
    }
}
