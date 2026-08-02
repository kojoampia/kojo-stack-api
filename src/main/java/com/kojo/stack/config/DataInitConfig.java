package com.kojo.stack.config;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kojo.stack.domain.model.Account;
import com.kojo.stack.domain.model.Authority;
import com.kojo.stack.domain.model.Doc;
import com.kojo.stack.domain.model.Education;
import com.kojo.stack.domain.model.Experience;
import com.kojo.stack.domain.model.Inquiry;
import com.kojo.stack.domain.model.Kpi;
import com.kojo.stack.domain.model.Metric;
import com.kojo.stack.domain.model.Project;
import com.kojo.stack.domain.model.Settings;
import com.kojo.stack.domain.model.TechSkill;
import com.kojo.stack.repository.AccountRepository;
import com.kojo.stack.repository.AuthorityRepository;
import com.kojo.stack.repository.DocRepository;
import com.kojo.stack.repository.EducationRepository;
import com.kojo.stack.repository.ExperienceRepository;
import com.kojo.stack.repository.InquiryRepository;
import com.kojo.stack.repository.KpiRepository;
import com.kojo.stack.repository.MetricRepository;
import com.kojo.stack.repository.ProfileRepository;
import com.kojo.stack.repository.ProjectRepository;
import com.kojo.stack.repository.SettingsRepository;
import com.kojo.stack.repository.SkillRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Data Initialization Configuration
 * Seeds database with sample data loaded from resources/data.json
 */
@Configuration
@Profile("!prod")
@RequiredArgsConstructor
@Slf4j
public class DataInitConfig {

    private final ExperienceRepository experienceRepository;
    private final ProjectRepository projectRepository;
    private final DocRepository docRepository;
    private final SkillRepository skillRepository;
    private final EducationRepository educationRepository;
    private final AuthorityRepository authorityRepository;
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final InquiryRepository inquiryRepository;
    private final SettingsRepository settingsRepository;
    private final MetricRepository metricRepository;
    private final KpiRepository kpiRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            JsonNode root = loadSeedData();
            if (root == null) {
                log.warn("Could not load seed data from data.json");
                return;
            }

            initializeAuthorities(root.path("authorities"));
            initializeAccounts(root.path("accounts"));
            initializeExperiences(root.path("experiences"));
            initializeSkills(root.path("tech_skills"));
            initializeProjects(root.path("projects"));
            initializeDocumentation(root.path("documentation"));
            initializeEducation(root.path("education"));
            initializeProfiles(root.path("profiles"));
            initializeInquiries(root.path("inquiries"));
            initializeSettings(root.path("settings"));
            initializeMetrics(root.path("metrics"));
            initializeKpis(root.path("kpis"));
        };
    }

    private JsonNode loadSeedData() {
        try (InputStream is = new ClassPathResource("data.json").getInputStream()) {
            return objectMapper.readTree(is);
        } catch (Exception e) {
            log.error("Failed to load data.json", e);
            return null;
        }
    }

    private void initializeAuthorities(JsonNode authoritiesNode) {
        if (authorityRepository.count() > 0) {
            return;
        }
        List<Authority> authorities = new ArrayList<>();
        for (JsonNode node : authoritiesNode) {
            authorities.add(Authority.builder()
                    .name(node.path("name").asText())
                    .build());
        }
        authorityRepository.saveAll(authorities);
    }

    /**
     * Seeds non-admin accounts only. The site administrator is created and rotated by
     * {@link AdminAccountInitializer} from the environment, so no credential is stored
     * in {@code data.json}; the accounts array there is empty by design.
     *
     * Accounts are only cleared when the seed file actually carries some, otherwise a
     * restart would delete the administrator this application depends on.
     */
    private void initializeAccounts(JsonNode accountsNode) {
        if (!accountsNode.isArray() || accountsNode.isEmpty()) {
            return;
        }
        accountRepository.deleteAll();
        for (JsonNode node : accountsNode) {
            Set<Authority> authorities = new HashSet<>();
            for (JsonNode authNode : node.path("authorities")) {
                authorityRepository.findByName(authNode.path("name").asText())
                        .ifPresent(authorities::add);
            }
            Account account = Account.builder()
                    .login(node.path("login").asText())
                    .email(node.path("email").asText())
                    .firstName(node.path("firstName").asText(null))
                    .lastName(node.path("lastName").asText(null))
                    .activated(node.path("activated").asBoolean(true))
                    .langKey(node.path("langKey").asText(null))
                    .imageUrl(node.path("imageUrl").asText(null))
                    .password(passwordEncoder.encode(node.path("password").asText()))
                    .authorities(authorities)
                    .build();
            accountRepository.save(account);
        }
    }

    private void initializeExperiences(JsonNode experiencesNode) {
        experienceRepository.deleteAll();
        List<Experience> experiences = new ArrayList<>();
        for (JsonNode node : experiencesNode) {
            List<Experience.Metric> metrics = new ArrayList<>();
            for (JsonNode m : node.path("metrics")) {
                metrics.add(Experience.Metric.builder()
                        .label(m.path("label").asText())
                        .value(m.path("value").asText())
                        .build());
            }
            experiences.add(Experience.builder()
                    .company(node.path("company").asText())
                    .role(node.path("role").asText())
                    .period(node.path("period").asText())
                    .status(Experience.StatusType.valueOf(node.path("status").asText().toUpperCase()))
                    .description(node.path("description").asText())
                    .stack(toStringList(node.path("stack")))
                    .metrics(metrics)
                    .build());
        }
        experienceRepository.saveAll(experiences);
    }

    private void initializeSkills(JsonNode skillsNode) {
        skillRepository.deleteAll();
        List<TechSkill> skills = new ArrayList<>();
        for (JsonNode node : skillsNode) {
            skills.add(TechSkill.builder()
                    .name(node.path("name").asText())
                    .category(node.path("category").asText())
                    .level(node.path("level").asInt())
                    .icon(node.path("icon").asText())
                    .build());
        }
        skillRepository.saveAll(skills);
    }

    private void initializeProjects(JsonNode projectsNode) {
        projectRepository.deleteAll();
        List<Project> projects = new ArrayList<>();
        for (JsonNode node : projectsNode) {
            Project.ProjectBuilder builder = Project.builder()
                    .name(node.path("name").asText())
                    .client(node.path("client").asText())
                    .type(Project.ProjectType.valueOf(node.path("type").asText().toUpperCase()))
                    .description(node.path("description").asText())
                    .stack(toStringList(node.path("stack")))
                    .status(Project.ProjectStatus.valueOf(node.path("status").asText().toUpperCase()))
                    .architecture(node.path("architecture").asText());
            if (node.has("startDate")) {
                builder.startDate(LocalDate.parse(node.path("startDate").asText()));
            }
            if (node.has("endDate")) {
                builder.endDate(LocalDate.parse(node.path("endDate").asText()));
            }
            projects.add(builder.build());
        }
        projectRepository.saveAll(projects);
    }

    private void initializeDocumentation(JsonNode docsNode) {
        docRepository.deleteAll();
        List<Doc> docs = new ArrayList<>();
        for (JsonNode node : docsNode) {
            Doc.DocBuilder builder = Doc.builder()
                    .title(node.path("title").asText())
                    .type(node.path("type").asText())
                    .tags(toStringList(node.path("tags")))
                    .content(node.path("content").asText());
            if (node.has("id")) {
                builder.id(node.path("id").asText());
            }
            if (node.has("lastUpdated")) {
                builder.lastUpdated(LocalDate.parse(node.path("lastUpdated").asText()));
            }
            docs.add(builder.build());
        }
        docRepository.saveAll(docs);
    }

    private void initializeEducation(JsonNode educationNode) {
        educationRepository.deleteAll();
        List<Education> educationList = new ArrayList<>();
        for (JsonNode node : educationNode) {
            educationList.add(Education.builder()
                    .institution(node.path("institution").asText())
                    .subjects(toStringList(node.path("subjects")))
                    .type(node.path("type").asText())
                    .duration(node.path("duration").asText())
                    .build());
        }
        educationRepository.saveAll(educationList);
    }

    private void initializeProfiles(JsonNode profilesNode) {
        profileRepository.deleteAll();
        List<com.kojo.stack.domain.model.Profile> profiles = new ArrayList<>();
        for (JsonNode node : profilesNode) {
            profiles.add(com.kojo.stack.domain.model.Profile.builder()
                    .name(node.path("name").asText())
                    .title(node.path("title").asText())
                    .email(node.path("email").asText())
                    .phone(node.path("phone").asText())
                    .location(node.path("location").asText())
                    .avatar(node.path("avatar").asText())
                    .build());
        }
        profileRepository.saveAll(profiles);
    }

    private void initializeInquiries(JsonNode inquiriesNode) {
        inquiryRepository.deleteAll();
        List<Inquiry> inquiries = new ArrayList<>();
        for (JsonNode node : inquiriesNode) {
            Inquiry.InquiryBuilder builder = Inquiry.builder()
                    .name(node.path("name").asText())
                    .email(node.path("email").asText())
                    .type(Inquiry.InquiryType.valueOf(node.path("type").asText().toUpperCase().replace(" ", "_")))
                    .message(node.path("message").asText())
                    .status(Inquiry.InquiryStatus.valueOf(node.path("status").asText().toUpperCase().replace(" ", "_")));
            if (node.has("id")) {
                builder.id(node.path("id").asText());
            }
            inquiries.add(builder.build());
        }
        inquiryRepository.saveAll(inquiries);
    }

    private void initializeSettings(JsonNode settingsNode) {
        settingsRepository.deleteAll();
        List<Settings> settingsList = new ArrayList<>();
        for (JsonNode node : settingsNode) {
            settingsList.add(Settings.builder()
                    .verboseLogging(node.path("verboseLogging").asBoolean())
                    .betaFeatures(node.path("betaFeatures").asBoolean())
                    .theme(node.path("theme").asText())
                    .language(node.path("language").asText())
                    .build());
        }
        settingsRepository.saveAll(settingsList);
    }

    private void initializeMetrics(JsonNode metricsNode) {
        metricRepository.deleteAll();
        List<Metric> metrics = new ArrayList<>();
        for (JsonNode node : metricsNode) {
            metrics.add(Metric.builder()
                    .label(node.path("label").asText())
                    .value(node.path("value").asText())
                    .trend(node.path("trend").asText())
                    .category(node.path("category").asText())
                    .description(node.path("description").asText())
                    .build());
        }
        metricRepository.saveAll(metrics);
    }

    private void initializeKpis(JsonNode kpisNode) {
        kpiRepository.deleteAll();
        List<Kpi> kpis = new ArrayList<>();
        for (JsonNode node : kpisNode) {
            kpis.add(Kpi.builder()
                    .label(node.path("label").asText())
                    .value(node.path("value").asText())
                    .unit(node.path("unit").asText(null))
                    .icon(node.path("icon").asText(null))
                    .color(node.path("color").asText(null))
                    .progress(node.has("progress") ? node.path("progress").asInt() : null)
                    .subtitle(node.path("subtitle").asText(null))
                    .sortOrder(node.has("sortOrder") ? node.path("sortOrder").asInt() : null)
                    .build());
        }
        kpiRepository.saveAll(kpis);
    }

    private List<String> toStringList(JsonNode arrayNode) {
        List<String> list = new ArrayList<>();
        for (JsonNode item : arrayNode) {
            list.add(item.asText());
        }
        return list;
    }
}
