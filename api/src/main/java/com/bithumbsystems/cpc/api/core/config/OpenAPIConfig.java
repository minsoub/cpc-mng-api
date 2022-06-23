package com.bithumbsystems.cpc.api.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import java.util.List;
import java.util.Map;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    var authHeader = Map.of("bearAuth",
        new SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
        .in(In.HEADER)
        .name("Authorization"));

    var schemaRequirement = new SecurityRequirement().addList("bearAuth");

    return new OpenAPI()
        .components(
            new Components()
                .securitySchemes(authHeader)
                .addParameters(
                    "user_ip", new HeaderParameter().required(false).name("user_ip").description("user_ip")
                        .schema(new StringSchema())
                )
                .addParameters(
                    "site_id", new HeaderParameter().required(false).name("site_id").description("site_id")
                        .schema(new StringSchema())
                )
        )
        .security(List.of(schemaRequirement));
  }

  @Bean
  public OpenApiCustomiser customerGlobalHeaderOpenApiCustomiser() {
    return openApi -> openApi.getPaths().values()
        .forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
              operation.addParametersItem(new HeaderParameter().$ref("#/components/parameters/user_ip"));
              operation.addParametersItem(new HeaderParameter().$ref("#/components/parameters/site_id"));
            }));
  }
}