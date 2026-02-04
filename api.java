package com.kojo.stack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Kojo.Stack Enterprise Backend
 * * Architecture:
 * - REST Controllers for Frontend Data
 * - Service Layer for Business Logic
 * - Repository Layer (In-Memory for portability, swap with MongoDB for Prod)
 * - Domain Models matching Angular Interfaces
 */
@SpringBootApplication
public class KojoStackApplication {

    public static void main(String[] args) {
        SpringApplication.run(KojoStackApplication.class, args);
    }

    // Global CORS configuration to allow Angular frontend
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200"); // Angular Default
        config.addAllowedOriginPattern("*"); // For Cloud IDEs
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

// ==========================================
// REST CONTROLLERS
// ==========================================

@RestController
@RequestMapping("/api/v1")
class DashboardController {

    private final DataService dataService;

    public DashboardController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/experiences")
    public List<Experience> getExperiences() {
        return dataService.getAllExperiences();
    }

    @GetMapping("/projects")
    public List<Project> getProjects() {
        return dataService.getAllProjects();
    }

    @PostMapping("/projects")
    public ResponseEntity<Project> createEngagement(@RequestBody Inquiry inquiry) {
        Project newProject = dataService.initializeEngagement(inquiry);
        return new ResponseEntity<>(newProject, HttpStatus.CREATED);
    }

    @GetMapping("/docs")
    public List<Doc> getDocs() {
        return dataService.getAllDocs();
    }

    @GetMapping("/skills")
    public List<TechSkill> getSkills() {
        return dataService.getAllSkills();
    }
}

// ==========================================
// SERVICE LAYER
// ==========================================

@Service
class DataService {

    // Simulating Database Collections
    private final List<Experience> experiences = new ArrayList<>();
    private final List<Project> projects = new ArrayList<>();
    private final List<Doc> docs = new ArrayList<>();
    private final List<TechSkill> skills = new ArrayList<>();

    @PostConstruct
    public void initData() {
        // --- 1. Load Experiences (Matching CV) ---
        experiences.add(new Experience(
            "Bundesrechenzentrum (BRZ)", 
            "Software Developer & DevOps Engineer", 
            "07/2025 - Present", 
            "Active", 
            "Refactoring core APIs and designing GitOps infrastructure. Implementing OpenTelemetry for full system observability.", 
            List.of("Java 21", "Spring Boot", "OpenTelemetry", "Kubernetes", "DB2", "Grafana"),
            List.of(new Metric("API Availability", "99.95%", "up"), new Metric("Latency", "<50ms", "stable"))
        ));
        
        experiences.add(new Experience(
            "Abofonsa Mobile Health", 
            "Lead Architect (Pro-bono)", 
            "03/2022 - Present", 
            "Active", 
            "Led development of a Near-Real Time communication brokerage platform for mobile health services.", 
            List.of("JHipster", "Kafka", "MongoDB", "Angular 17", "Docker"),
            List.of(new Metric("Msg Throughput", "5k/sec", "up"), new Metric("Users", "Scaling", "up"))
        ));

        // ... Add older jobs here ...

        // --- 2. Load Projects ---
        projects.add(new Project("Health Brokerage Platform", "Abofonsa Mobile Health", "Microservices", 
            "Near-real time communication brokerage using Kafka streams.", 
            List.of("Java 17", "Kafka", "MongoDB", "Angular 17"), "Live", "Event-Driven"));

        projects.add(new Project("Enterprise GitOps Pipeline", "BRZ", "DevOps", 
            "Infrastructure as Code design and OpenTelemetry integration.", 
            List.of("K8s", "ArgoCD", "Java 21"), "Live", "Cloud Native"));

        // --- 3. Load Docs ---
        docs.add(new Doc("ADR-2024-001", "Event-Driven Architecture Strategy", "ADR", 
            List.of("Kafka", "Microservices"), "2024-03-15", "Decided to implement Kafka for decoupling services..."));

        // --- 4. Load Skills ---
        skills.add(new TechSkill("Java 21", "Backend", 100, "java"));
        skills.add(new TechSkill("Angular", "Frontend", 95, "angular"));
        skills.add(new TechSkill("Kubernetes", "DevOps", 95, "dharmachakra"));
    }

    public List<Experience> getAllExperiences() { return experiences; }
    public List<Project> getAllProjects() { return projects; }
    public List<Doc> getAllDocs() { return docs; }
    public List<TechSkill> getAllSkills() { return skills; }

    /**
     * Handles the "Hire Consultant" form submission.
     * In a real app, this would publish a Kafka Event: engagement.initialized
     */
    public Project initializeEngagement(Inquiry inquiry) {
        Project newProject = new Project(
            inquiry.type() + " Strategy",
            inquiry.name(),
            inquiry.type(), // Maps to Type
            inquiry.message(),
            List.of("Planning", "TBD"),
            "Pending", // Initial Status
            "Under Review"
        );
        
        // Simulating DB Persistence
        // In Prod: projectRepository.save(newProject);
        // In Prod: kafkaTemplate.send("engagement-events", newProject);
        
        // Add to top of list for UI to see immediately
        this.projects.add(0, newProject);
        
        return newProject;
    }
}

// ==========================================
// DOMAIN MODELS (POJOs)
// ==========================================

// Records are perfect for immutable DTOs in Java 17+
record Metric(String label, String value, String trend) {}

class Experience {
    public String company;
    public String role;
    public String period;
    public String status;
    public String description;
    public List<String> stack;
    public List<Metric> metrics;

    public Experience(String company, String role, String period, String status, String description, List<String> stack, List<Metric> metrics) {
        this.company = company;
        this.role = role;
        this.period = period;
        this.status = status;
        this.description = description;
        this.stack = stack;
        this.metrics = metrics;
    }
}

class Project {
    public String name;
    public String client;
    public String type;
    public String description;
    public List<String> stack;
    public String status;
    public String architecture;

    public Project(String name, String client, String type, String description, List<String> stack, String status, String architecture) {
        this.name = name;
        this.client = client;
        this.type = type;
        this.description = description;
        this.stack = stack;
        this.status = status;
        this.architecture = architecture;
    }
}

record Doc(String id, String title, String type, List<String> tags, String lastUpdated, String content) {}

record TechSkill(String name, String category, int level, String icon) {}

record Inquiry(String name, String email, String type, String message) {}