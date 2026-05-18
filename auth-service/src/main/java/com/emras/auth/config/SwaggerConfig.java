package com.emras.auth.config;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Swagger / OpenAPI 3 configuration.
 * Swagger UI available at: http://localhost:8081/swagger-ui.html
 * OpenAPI spec at:         http://localhost:8081/v3/api-docs
 *
 * The JWT bearer scheme is pre-configured so testers can paste a token
 * directly into the Swagger UI Authorize button and test secured endpoints.
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI authServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Emras — Auth Service API")
                        .description("Authentication and authorization endpoints for the Emras e-commerce platform.")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Emras Backend Team")
                                .email("dev@emras.com")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste your JWT access token here. Obtain it from POST /api/v1/auth/login")));
    }
}