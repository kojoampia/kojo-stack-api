package com.kojo.stack.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for {@link JwtTokenProvider}.
 *
 * Two behaviours here are load-bearing for authorization:
 * the authorities claim must survive a round trip (otherwise every role check
 * denies), and a missing or undersized secret must stop the application from
 * starting (otherwise it silently signs with a weak or default key).
 */
class JwtTokenProviderTest {

    private static final String VALID_SECRET = "unit-test-signing-key-with-more-than-32-bytes";

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", VALID_SECRET);
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationMs", 60_000L);
    }

    @Test
    @DisplayName("a generated token round-trips its subject")
    void tokenCarriesSubject() {
        String token = tokenProvider.generateToken("kojo");

        assertThat(tokenProvider.validateToken(token)).isTrue();
        assertThat(tokenProvider.getUsernameFromToken(token)).isEqualTo("kojo");
    }

    @Test
    @DisplayName("authorities from an Authentication survive the round trip")
    void tokenCarriesAuthoritiesFromAuthentication() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "admin", null,
                List.of(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN),
                        new SimpleGrantedAuthority(AuthoritiesConstants.USER)));

        String token = tokenProvider.generateToken(authentication);

        assertThat(tokenProvider.getAuthoritiesFromToken(token))
                .containsExactlyInAnyOrder(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER);
    }

    @Test
    @DisplayName("explicit authorities survive the round trip")
    void tokenCarriesExplicitAuthorities() {
        String token = tokenProvider.generateToken("admin", List.of(AuthoritiesConstants.ADMIN));

        assertThat(tokenProvider.getAuthoritiesFromToken(token))
                .containsExactly(AuthoritiesConstants.ADMIN);
    }

    @Test
    @DisplayName("a token with no authorities yields an empty list rather than failing")
    void tokenWithoutAuthoritiesYieldsEmptyList() {
        String token = tokenProvider.generateToken("anonymous");

        assertThat(tokenProvider.getAuthoritiesFromToken(token)).isEmpty();
    }

    @Test
    @DisplayName("a token signed with a different secret is rejected")
    void tokenSignedWithAnotherSecretIsRejected() {
        String foreignToken = tokenProvider.generateToken("attacker", List.of(AuthoritiesConstants.ADMIN));

        JwtTokenProvider other = new JwtTokenProvider();
        ReflectionTestUtils.setField(other, "jwtSecret", "a-completely-different-key-of-sufficient-length");
        ReflectionTestUtils.setField(other, "jwtExpirationMs", 60_000L);

        assertThat(other.validateToken(foreignToken)).isFalse();
    }

    @Test
    @DisplayName("an expired token is rejected")
    void expiredTokenIsRejected() {
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationMs", -1_000L);

        String token = tokenProvider.generateToken("kojo");

        assertThat(tokenProvider.validateToken(token)).isFalse();
    }

    @Test
    @DisplayName("startup fails when no secret is configured")
    void blankSecretIsRejectedAtStartup() {
        JwtTokenProvider provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "jwtSecret", "");

        assertThatThrownBy(provider::validateSecret)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT_SECRET");
    }

    @Test
    @DisplayName("startup fails when the secret is too short for HS256")
    void shortSecretIsRejectedAtStartup() {
        JwtTokenProvider provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "jwtSecret", "too-short");

        assertThatThrownBy(provider::validateSecret)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least 32 bytes");
    }

    @Test
    @DisplayName("a sufficiently long secret is accepted at startup")
    void validSecretIsAcceptedAtStartup() {
        JwtTokenProvider provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "jwtSecret", VALID_SECRET);

        provider.validateSecret();
    }
}
