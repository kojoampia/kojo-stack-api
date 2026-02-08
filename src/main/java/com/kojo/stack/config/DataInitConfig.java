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
    private final EducationRepository educationRepository;
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
            initializeEducation();
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
                        .period("07/2024 - Present")
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
                        .company("Accenture")
                        .role("Senior DevOps Engineer")
                        .period("06/2022 - 08/2023")
                        .status(Experience.StatusType.STABLE)
                        .description("Application Migration to OpenShift PaaS. Designed custom metrics for SLI/SLO monitoring using PromQL.")
                        .stack(List.of("OpenShift", "Helm", "Jenkins", "Prometheus", "ELK Stack"))
                        .build(),

                Experience.builder()
                        .company("Bedrock Insurance")
                        .role("Senior Software Architect")
                        .period("08/2020 - 12/2022")
                        .status(Experience.StatusType.STABLE)
                        .description("Developed Owasp T10 compliant web application with dashboards for Operations and Business Admin.")
                        .stack(List.of("Java 11", "Microservices", "Spring Security", "Angular 10"))
                        .build(),

                Experience.builder()
                        .company("Austrian Railways (ÖBB)")
                        .role("Monitoring & DevOps Engineer")
                        .period("02/2019 - 10/2021")
                        .status(Experience.StatusType.DEPRECATED)
                        .description("Ticketshop Operations Monitoring. Built dashboards using Elastic Stack and customized alert configurations.")
                        .stack(List.of("Elasticsearch", "Grafana", "OracleDB", "Angular"))
                        .build()
        );
        experienceRepository.saveAll(experiences);
    }

    private void initializeSkills() {
        List<TechSkill> skills = List.of(
                TechSkill.builder().name("Java 8/11/17/21").category("Backend").level(100).icon("java").build(),
                TechSkill.builder().name("Spring Boot & Cloud").category("Backend").level(95).icon("leaf").build(),
                TechSkill.builder().name("Microservices").category("Backend").level(90).icon("network-wired").build(),
                TechSkill.builder().name("JHipster").category("Backend").level(90).icon("rocket").build(),
                TechSkill.builder().name("Angular (10-17+)").category("Frontend").level(95).icon("angular").build(),
                TechSkill.builder().name("TypeScript").category("Frontend").level(90).icon("code").build(),
                TechSkill.builder().name("Kubernetes & Helm").category("DevOps").level(95).icon("dharmachakra").build(),
                TechSkill.builder().name("Docker").category("DevOps").level(95).icon("docker").build(),
                TechSkill.builder().name("Jenkins CI/CD").category("DevOps").level(90).icon("cogs").build(),
                TechSkill.builder().name("OpenShift").category("DevOps").level(85).icon("cloud").build(),
                TechSkill.builder().name("Apache Kafka").category("Data").level(90).icon("stream").build(),
                TechSkill.builder().name("Elasticsearch/ELK").category("Data").level(85).icon("search").build(),
                TechSkill.builder().name("SQL (Postgres/Oracle)").category("Data").level(90).icon("database").build(),
                TechSkill.builder().name("MongoDB").category("Data").level(90).icon("leaf").build()
        );
        skillRepository.saveAll(skills);
    }

    private void initializeProjects() {
        List<Project> projects = List.of(
                Project.builder()
                        .name("Health Brokerage Platform")
                        .client("Abofonsa Mobile Health")
                        .type(Project.ProjectType.MICROSERVICES)
                        .description("Near-real time communication brokerage using Kafka streams for mobile health data processing.")
                        .stack(List.of("Java 17", "Kafka", "MongoDB", "JHipster", "Angular 17"))
                        .status(Project.ProjectStatus.ACTIVE)
                        .architecture("Event-Driven")
                        .startDate(LocalDate.of(2022, 3, 1))
                        .build(),

                Project.builder()
                        .name("Enterprise GitOps Pipeline")
                        .client("BRZ")
                        .type(Project.ProjectType.DEVOPS)
                        .description("Infrastructure as Code design and OpenTelemetry integration for federal computing services.")
                        .stack(List.of("Kubernetes", "ArgoCD", "OpenTelemetry", "Java 21"))
                        .status(Project.ProjectStatus.ACTIVE)
                        .architecture("Cloud Native")
                        .startDate(LocalDate.of(2024, 7, 1))
                        .build(),

                Project.builder()
                        .name("Insurance Portal (OWASP)")
                        .client("Bedrock Insurance")
                        .type(Project.ProjectType.MICROSERVICES)
                        .description("Secure policy management dashboards and public facing website compliant with OWASP Top 10 standards.")
                        .stack(List.of("Spring Security", "Angular 10", "JWT", "Netflix OSS"))
                        .status(Project.ProjectStatus.MAINTENANCE)
                        .architecture("Microservices")
                        .startDate(LocalDate.of(2020, 8, 1))
                        .endDate(LocalDate.of(2022, 12, 31))
                        .build(),

                Project.builder()
                        .name("OpenShift Cloud Migration")
                        .client("Accenture")
                        .type(Project.ProjectType.MIGRATION)
                        .description("Large-scale migration of legacy applications to OpenShift PaaS with custom SLI/SLO monitoring.")
                        .stack(List.of("OpenShift", "Helm", "PromQL", "Groovy"))
                        .status(Project.ProjectStatus.COMPLETED)
                        .architecture("Hybrid Cloud")
                        .startDate(LocalDate.of(2022, 6, 1))
                        .endDate(LocalDate.of(2023, 8, 31))
                        .build(),

                Project.builder()
                        .name("Ticketshop Monitoring")
                        .client("ÖBB")
                        .type(Project.ProjectType.MONITORING)
                        .description("High-availability monitoring solution for national rail ticketing operations using Elastic Stack.")
                        .stack(List.of("Elasticsearch", "Grafana", "OracleDB"))
                        .status(Project.ProjectStatus.COMPLETED)
                        .architecture("Observability")
                        .startDate(LocalDate.of(2019, 2, 1))
                        .endDate(LocalDate.of(2021, 10, 31))
                        .build(),

                Project.builder()
                        .name("Interconnection Billing")
                        .client("T-Mobile Austria")
                        .type(Project.ProjectType.ETL)
                        .description("Optimization of routine operations for the Interconnection Billing Process implementation on Mediation Zone.")
                        .stack(List.of("Java 6", "Oracle DB", "PL/SQL", "Cognos"))
                        .status(Project.ProjectStatus.COMPLETED)
                        .architecture("Legacy")
                        .startDate(LocalDate.of(2010, 1, 1))
                        .endDate(LocalDate.of(2015, 12, 31))
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

    private void initializeEducation() {
        List<Education> educationList = List.of(
                Education.builder()
                        .institution("Computer, Munich - Germany")
                        .subjects(List.of("Monitoring and Security"))
                        .type("Professional Workshop")
                        .duration("June, 2019")
                        .build(),

                Education.builder()
                        .institution("Thales Group, Vienna - Austria")
                        .subjects(List.of("Data Security and Privacy"))
                        .type("Professional Study")
                        .duration("June, 2017")
                        .build(),

                Education.builder()
                        .institution("Thales Group, Vienna - Austria")
                        .subjects(List.of("Railway Signaling and Control"))
                        .type("Professional Workshop")
                        .duration("June, 2016")
                        .build(),

                Education.builder()
                        .institution("Thales Group, Vienna - Austria")
                        .subjects(List.of("Cyber Security"))
                        .type("Professional Study")
                        .duration("June, 2015")
                        .build(),

                Education.builder()
                        .institution("University of Technology (TU Wien), Vienna - Austria")
                        .subjects(List.of("Computer Science", "Economics"))
                        .type("University Bachelor Education")
                        .duration("October, 2004 - June 2010")
                        .build(),

                Education.builder()
                        .institution("Vorstudienlehrgang der Wieneruniversitäten, Vienna - Austria")
                        .subjects(List.of("Mathematics", "Physics", "German"))
                        .type("Pre University School")
                        .duration("October, 2002 - June 2004")
                        .build(),

                Education.builder()
                        .institution("Mfantsipim School, Cape Coast - Ghana")
                        .subjects(List.of("Mathematics", "Geography", "Economics", "General Paper"))
                        .type("Secondary Advance Level")
                        .duration("October, 1993 - June 1995")
                        .build(),

                Education.builder()
                        .institution("Mfantsipim School, Cape Coast - Ghana")
                        .subjects(List.of("Advanced Mathematics", "Physics", "Mathematics", "Chemistry", "Biology", "English", "Technical Drawing", "Geography"))
                        .type("Secondary GCE Ordinary Level")
                        .duration("September, 1987 - June 1992")
                        .build()
        );
        educationRepository.saveAll(educationList);
    }
}
