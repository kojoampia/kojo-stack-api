package com.kojo.stack.config;

import com.kojo.stack.domain.model.*;
import com.kojo.stack.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Set;
import java.util.List;

/**
 * Data Initialization Configuration
 * Seeds database with sample data for development and testing
 */
@Configuration
@Profile("!prod")  // Don't run in production
@RequiredArgsConstructor
public class DataInitConfig {

    private final ExperienceRepository experienceRepository;
    private final ProjectRepository projectRepository;
    private final DocRepository docRepository;
    private final SkillRepository skillRepository;
    private final AuthorityRepository authorityRepository;
    private final AccountRepository AccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            // Initialize authorities first
            initializeAuthorities();
            
            // Initialize user with admin role
            initializeAdminUser();
            
            // Only initialize other data if empty
            if (experienceRepository.count() > 0) {
                return;
            }

            initializeExperiences();
            initializeSkills();
            initializeProjects();
            initializeDocumentation();
        };
    }

    private void initializeAuthorities() {
        // Only initialize if empty
        if (authorityRepository.count() > 0) {
            return;
        }

        List<Authority> authorities = List.of(
                Authority.builder().name("ROLE_ADMIN").build(),
                Authority.builder().name("ROLE_USER").build(),
                Authority.builder().name("ROLE_VIEWER").build()
        );
        authorityRepository.saveAll(authorities);
    }

    private void initializeAdminUser() {
        // Only initialize if admin doesn't exist
        // if (AccountRepository.findByLogin("admin").isPresent()) {
        //    return;
        // }

        // Check if admin authority exists
        // Get the ADMIN authority
        Authority adminAuthority = authorityRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> authorityRepository.save(
                        Authority.builder().name("ROLE_ADMIN").build()
                ));

        
        AccountRepository.deleteAll();        

        // Create default admin user
        Account adminUser = Account.builder()
                .login("admin")
                .email("admin@jojoaddison.net")
                .password(passwordEncoder.encode("Moba1992!"))
                .authorities(Set.of(adminAuthority))
                .build();

        AccountRepository.save(adminUser);
    }

    private void initializeExperiences() {
        List<Experience> experiences = List.of(
                Experience.builder()
                        .company("Bundesrechenzentrum (BRZ)")
                        .role("Software Developer & DevOps Engineer")
                        .period("07/2025 - Present")
                        .status(Experience.StatusType.ACTIVE)
                        .description("Refactoring core APIs and designing GitOps infrastructure. Implementing OpenTelemetry for full system observability.")
                        .stack(List.of("Java 21", "Spring Boot", "OpenTelemetry", "Kubernetes", "DB2", "Grafana"))
                        .build(),

                Experience.builder()
                        .company("Abofonsa Mobile Health")
                        .role("Lead Architect (Pro-bono)")
                        .period("03/2022 - Present")
                        .status(Experience.StatusType.ACTIVE)
                        .description("Led development of a Near-Real Time communication brokerage platform for mobile health services.")
                        .stack(List.of("JHipster", "Kafka", "MongoDB", "Angular 17", "Docker"))
                        .build(),

                Experience.builder()
                        .company("Acme Fintech Solutions")
                        .role("Senior Backend Engineer")
                        .period("01/2021 - 06/2025")
                        .status(Experience.StatusType.COMPLETED)
                        .description("Built high-performance trading APIs and event-driven microservices. Mentored junior engineers.")
                        .stack(List.of("Java 17", "Spring Cloud", "PostgreSQL", "RabbitMQ", "Kubernetes"))
                        .build()
        );
        experienceRepository.saveAll(experiences);
    }

    private void initializeSkills() {
        List<TechSkill> skills = List.of(
                TechSkill.builder().name("Java 21").category("Backend").level(100).icon("java").build(),
                TechSkill.builder().name("Spring Boot").category("Backend").level(98).icon("spring").build(),
                TechSkill.builder().name("Kubernetes").category("DevOps").level(95).icon("dharmachakra").build(),
                TechSkill.builder().name("Angular").category("Frontend").level(92).icon("angular").build(),
                TechSkill.builder().name("PostgreSQL").category("Database").level(90).icon("database").build(),
                TechSkill.builder().name("Kafka").category("Event-Streaming").level(88).icon("kafka").build(),
                TechSkill.builder().name("Docker").category("DevOps").level(95).icon("docker").build(),
                TechSkill.builder().name("Redis").category("Caching").level(85).icon("redis").build(),
                TechSkill.builder().name("MongoDB").category("NoSQL").level(82).icon("leaf").build(),
                TechSkill.builder().name("AWS").category("Cloud").level(80).icon("cloud").build()
        );
        skillRepository.saveAll(skills);
    }

    private void initializeProjects() {
        List<Project> projects = List.of(
                Project.builder()
                        .name("Health Brokerage Platform")
                        .client("Abofonsa Mobile Health")
                        .type(Project.ProjectType.MICROSERVICES)
                        .description("Near-real time communication brokerage using Kafka streams for mobile health services.")
                        .stack(List.of("Java 17", "Kafka", "MongoDB", "Angular 17", "Docker"))
                        .status(Project.ProjectStatus.ACTIVE)
                        .architecture("Event-Driven")
                        .startDate(LocalDate.of(2022, 3, 1))
                        .build(),

                Project.builder()
                        .name("Enterprise GitOps Pipeline")
                        .client("Bundesrechenzentrum (BRZ)")
                        .type(Project.ProjectType.DEVOPS)
                        .description("Infrastructure as Code design and OpenTelemetry integration for enterprise systems.")
                        .stack(List.of("Kubernetes", "ArgoCD", "Java 21", "Grafana"))
                        .status(Project.ProjectStatus.ACTIVE)
                        .architecture("Cloud-Native")
                        .startDate(LocalDate.of(2025, 7, 1))
                        .build(),

                Project.builder()
                        .name("Real-Time Trading API")
                        .client("Acme Fintech Solutions")
                        .type(Project.ProjectType.BACKEND_SERVICE)
                        .description("High-performance REST API for financial trading with sub-millisecond latency.")
                        .stack(List.of("Java 17", "Spring Boot", "PostgreSQL", "Redis"))
                        .status(Project.ProjectStatus.COMPLETED)
                        .architecture("Microservices")
                        .startDate(LocalDate.of(2021, 1, 1))
                        .endDate(LocalDate.of(2025, 6, 30))
                        .build()
        );
        projectRepository.saveAll(projects);
    }

    private void initializeDocumentation() {
        List<Doc> docs = List.of(
                Doc.builder()
                        .id("ADR-2024-001")
                        .title("Event-Driven Architecture Strategy")
                        .type("ADR")
                        .tags(List.of("Kafka", "Microservices", "Architecture"))
                        .content("Decided to implement Kafka for decoupling services and enabling event-driven communication across the platform.")
                        .lastUpdated(LocalDate.of(2024, 3, 15))
                        .build(),

                Doc.builder()
                        .id("ARCH-2024-002")
                        .title("Microservices Design Patterns")
                        .type("Architecture")
                        .tags(List.of("Spring Boot", "Kubernetes", "Design Patterns"))
                        .content("Documentation on implementing circuit breakers, saga patterns, and distributed tracing in microservices.")
                        .lastUpdated(LocalDate.of(2024, 6, 10))
                        .build(),

                Doc.builder()
                        .id("OPS-2024-003")
                        .title("Kubernetes Deployment Guide")
                        .type("Operations")
                        .tags(List.of("Kubernetes", "Docker", "DevOps"))
                        .content("Complete guide for deploying Spring Boot applications on Kubernetes with auto-scaling and health checks.")
                        .lastUpdated(LocalDate.of(2024, 9, 20))
                        .build()
        );
        docRepository.saveAll(docs);
    }
}
