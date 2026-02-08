package com.kojo.stack;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Kojo.Stack Enterprise Backend Application
 *
 * Architecture:
 * - REST Controllers (HTTP Layer)
 * - Service Layer (Business Logic)
 * - Repository Layer (Data Persistence)
 * - Domain Models (JPA Entities)
 * - DTOs (Data Transfer Objects)
 * - Event-Driven Architecture (Kafka)
 * - Caching Layer (Ehcache - In-Memory)
 * - API Documentation (OpenAPI/Swagger)
 *
 * Technologies:
 * - Java 21
 * - Spring Boot 3.2
 * - Spring Data JPA (PostgreSQL/MongoDB)
 * - Spring Kafka (Event Streaming)
 * - Spring Cache (Ehcache)
 * - Lombok (Boilerplate Reduction)
 * - MapStruct (DTO Mapping)
 * - OpenAPI 3.0 (API Documentation)
 */
@SpringBootApplication
@EnableKafka
@EnableAsync
public class KojoStackApplication {

    public static void main(String[] args) {
        SpringApplication.run(KojoStackApplication.class, args);
    }

    /**
     * OpenAPI Configuration
     * Provides API documentation accessible at /swagger-ui.html
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kojo.Stack API")
                        .version("2026.1.0")
                        .description("Production-Ready REST API for Enterprise Consulting Platform")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("Kojo Addison")
                                .url("https://kojo.dev"))
                        .license(new io.swagger.v3.oas.models.info.License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
