package com.kojo.stack.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.kojo.stack.api.controller.AccountController;
import com.kojo.stack.api.controller.InquiryController;
import com.kojo.stack.api.controller.ProjectController;
import com.kojo.stack.api.dto.AccountDTO;
import com.kojo.stack.api.dto.AuthorityDTO;
import com.kojo.stack.api.dto.InquiryDTO;
import com.kojo.stack.config.CorsConfig;
import com.kojo.stack.config.RequestLoggingFilter;
import com.kojo.stack.config.SecurityConfig;
import com.kojo.stack.service.AccountService;
import com.kojo.stack.service.InquiryService;
import com.kojo.stack.service.ProjectService;

/**
 * Regression tests for the authorization rules in {@link SecurityConfig}.
 *
 * These exist because of a real exposure: a blanket {@code permitAll} on
 * {@code GET /api/v1/**} combined with a missing {@code @EnableMethodSecurity}
 * made every account record - including BCrypt password hashes - readable by
 * anonymous callers, since the {@code @PreAuthorize} annotations were inert.
 *
 * Tokens here are minted by the real {@link JwtTokenProvider} rather than by
 * {@code @WithMockUser}, so these tests also prove that the authorities claim
 * survives the round trip through {@link JwtAuthenticationFilter}.
 */
@WebMvcTest(
        controllers = { AccountController.class, InquiryController.class, ProjectController.class },
        // RequestLoggingFilter is a @Component filter that needs an OpenTelemetry Tracer,
        // which the web slice does not autoconfigure. It is irrelevant to authorization.
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE, classes = RequestLoggingFilter.class))
@Import({ SecurityConfig.class, CorsConfig.class, JwtTokenProvider.class })
@TestPropertySource(properties = {
        "app.jwtSecret=test-only-signing-key-with-at-least-32-bytes-of-entropy",
        "app.jwtExpirationMs=60000",
        "app.mail.from=test@example.com",
        "app.mail.base-url=http://localhost:4200"
})
class SecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @MockBean
    private AccountService accountService;

    @MockBean
    private InquiryService inquiryService;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private AuthenticationManager authenticationManager;

    private String adminToken() {
        return tokenProvider.generateToken("admin", List.of(AuthoritiesConstants.ADMIN));
    }

    private String userToken() {
        return tokenProvider.generateToken("regular", List.of(AuthoritiesConstants.USER));
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }

    // --- Account endpoints -------------------------------------------------

    @Test
    @DisplayName("anonymous callers cannot list accounts")
    void anonymousCannotListAccounts() throws Exception {
        mockMvc.perform(get("/api/v1/account/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("anonymous callers cannot look an account up by login")
    void anonymousCannotReadAccountByLogin() throws Exception {
        mockMvc.perform(get("/api/v1/account/login/admin"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("anonymous callers cannot look an account up by email")
    void anonymousCannotReadAccountByEmail() throws Exception {
        mockMvc.perform(get("/api/v1/account/email/admin@example.com"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("a non-admin authenticated user cannot list accounts")
    void nonAdminCannotListAccounts() throws Exception {
        mockMvc.perform(get("/api/v1/account/all").header("Authorization", bearer(userToken())))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("an admin can list accounts, and the response carries no credential material")
    void adminCanListAccountsWithoutCredentials() throws Exception {
        AccountDTO dto = AccountDTO.builder()
                .id("1")
                .login("admin")
                .email("admin@example.com")
                .authorities(Set.of(AuthorityDTO.builder().id("a1").name(AuthoritiesConstants.ADMIN).build()))
                .activated(true)
                .build();
        org.mockito.BDDMockito.given(accountService.getAll()).willReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/account/all").header("Authorization", bearer(adminToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("admin"))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[0].activationKey").doesNotExist())
                .andExpect(jsonPath("$[0].resetKey").doesNotExist());
    }

    @Test
    @DisplayName("a password set on the DTO is still never serialized to the client")
    void passwordIsNeverSerialized() throws Exception {
        AccountDTO leaky = AccountDTO.builder()
                .id("1")
                .login("admin")
                .email("admin@example.com")
                .password("$2a$10$notarealhashbutlooksliketone")
                .activationKey("activation-key")
                .resetKey("reset-key")
                .build();
        org.mockito.BDDMockito.given(accountService.getAll()).willReturn(List.of(leaky));

        mockMvc.perform(get("/api/v1/account/all").header("Authorization", bearer(adminToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[0].activationKey").doesNotExist())
                .andExpect(jsonPath("$[0].resetKey").doesNotExist());
    }

    // --- Inquiry endpoints -------------------------------------------------

    @Test
    @DisplayName("anonymous callers cannot read customer inquiries")
    void anonymousCannotReadInquiries() throws Exception {
        mockMvc.perform(get("/api/v1/inquiries"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("a non-admin authenticated user cannot read customer inquiries")
    void nonAdminCannotReadInquiries() throws Exception {
        mockMvc.perform(get("/api/v1/inquiries").header("Authorization", bearer(userToken())))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("an admin can read customer inquiries")
    void adminCanReadInquiries() throws Exception {
        org.mockito.BDDMockito.given(inquiryService.getAll()).willReturn(List.of());

        mockMvc.perform(get("/api/v1/inquiries").header("Authorization", bearer(adminToken())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("the public hire form can still submit an inquiry anonymously")
    void anonymousCanSubmitInquiry() throws Exception {
        InquiryDTO submitted = InquiryDTO.builder()
                .id("i1")
                .name("Jane Client")
                .email("jane@example.com")
                .type("CONSULTING")
                .message("I would like to discuss an engagement.")
                .build();
        org.mockito.BDDMockito.given(inquiryService.submitInquiry(org.mockito.ArgumentMatchers.any()))
                .willReturn(submitted);

        mockMvc.perform(post("/api/v1/inquiries/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Jane Client",
                                  "email": "jane@example.com",
                                  "type": "CONSULTING",
                                  "message": "I would like to discuss an engagement."
                                }
                                """))
                .andExpect(status().isCreated());
    }

    // --- Public portfolio content ------------------------------------------

    @Test
    @DisplayName("public portfolio content stays readable anonymously")
    void anonymousCanReadProjects() throws Exception {
        org.mockito.BDDMockito.given(projectService.getAll()).willReturn(List.of());

        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("writes to public content still require authentication")
    void anonymousCannotCreateProjects() throws Exception {
        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"x\"}"))
                .andExpect(status().isUnauthorized());
    }
}
