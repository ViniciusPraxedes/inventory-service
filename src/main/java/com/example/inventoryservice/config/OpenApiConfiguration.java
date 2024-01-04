package com.example.inventoryservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "Bearer Authorization";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description("Please insert JWT token")
                                )
                )
                .addServersItem(new Server().url("/"))
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Inventory service")
                .description("Internbooks inventory service. Create endpoint and delete endpoint are only accessible from the book service. When you create or delete a book, it will create/delete the inventory item, but you can not create/delete an inventory item directly in the inventory api." +
                        "To use this service, a jwt with admin role is required. to get the jwt token you need to login to your account in the user-service. You can use this account to log in, in case you are not an admin user: login: vinipraxedes@hotmail.com. Password: secretpassword")
                .version("1.0");
    }
}

