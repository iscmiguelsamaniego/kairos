package com.evaluacion.kairos.infrastructure.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kairos TV Maze Middleware API")
                        .version("v3.0")
                        .description("API middleware para consulta de shows de TV Maze enriquecidos con comentarios y persistencia en MongoDB."));
    }
}