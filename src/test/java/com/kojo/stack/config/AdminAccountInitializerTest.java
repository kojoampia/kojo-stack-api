package com.kojo.stack.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.kojo.stack.domain.model.Account;
import com.kojo.stack.domain.model.Authority;
import com.kojo.stack.repository.AccountRepository;
import com.kojo.stack.repository.AuthorityRepository;
import com.kojo.stack.security.AuthoritiesConstants;

/**
 * Unit tests for {@link AdminAccountInitializer}.
 *
 * The administrator credential previously lived as a literal in three checked-in
 * files. These tests pin the replacement: it comes from the environment, it is
 * always stored encoded, and changing it rotates the stored password.
 */
@ExtendWith(MockitoExtension.class)
class AdminAccountInitializerTest {

    private static final String PASSWORD = "a-sufficiently-long-admin-password";

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @InjectMocks
    private AdminAccountInitializer initializer;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(initializer, "passwordEncoder", encoder);
        ReflectionTestUtils.setField(initializer, "adminLogin", "admin");
        ReflectionTestUtils.setField(initializer, "adminEmail", "admin@example.com");
        ReflectionTestUtils.setField(initializer, "adminPassword", PASSWORD);
        ReflectionTestUtils.setField(initializer, "adminFirstName", "Site");
        ReflectionTestUtils.setField(initializer, "adminLastName", "Administrator");
    }

    private void stubAuthorities() {
        given(authorityRepository.findByName(AuthoritiesConstants.ADMIN))
                .willReturn(Optional.of(Authority.builder().id("a1").name(AuthoritiesConstants.ADMIN).build()));
        given(authorityRepository.findByName(AuthoritiesConstants.USER))
                .willReturn(Optional.of(Authority.builder().id("a2").name(AuthoritiesConstants.USER).build()));
    }

    @Test
    @DisplayName("creates the administrator when none exists, storing an encoded password")
    void createsAdminWhenAbsent() {
        stubAuthorities();
        given(accountRepository.findByLogin("admin")).willReturn(Optional.empty());

        initializer.run(null);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        Account saved = captor.getValue();

        assertThat(saved.getLogin()).isEqualTo("admin");
        assertThat(saved.getEmail()).isEqualTo("admin@example.com");
        assertThat(saved.isActivated()).isTrue();
        assertThat(saved.getPassword()).isNotEqualTo(PASSWORD);
        assertThat(encoder.matches(PASSWORD, saved.getPassword())).isTrue();
    }

    @Test
    @DisplayName("the created administrator holds ROLE_ADMIN")
    void createdAdminHoldsAdminAuthority() {
        stubAuthorities();
        given(accountRepository.findByLogin("admin")).willReturn(Optional.empty());

        initializer.run(null);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());

        assertThat(captor.getValue().getAuthorities())
                .extracting(Authority::getName)
                .containsExactlyInAnyOrder(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER);
    }

    @Test
    @DisplayName("creates the ROLE_ADMIN authority when the collection is empty")
    void createsMissingAuthorities() {
        given(authorityRepository.findByName(any())).willReturn(Optional.empty());
        given(authorityRepository.save(any(Authority.class))).willAnswer(inv -> inv.getArgument(0));
        given(accountRepository.findByLogin("admin")).willReturn(Optional.empty());

        initializer.run(null);

        verify(authorityRepository, org.mockito.Mockito.times(2)).save(any(Authority.class));
    }

    @Test
    @DisplayName("rotates the stored password when the configured one changes")
    void rotatesPasswordWhenChanged() {
        stubAuthorities();
        Account existing = Account.builder()
                .id("acc-1")
                .login("admin")
                .password(encoder.encode("the-previous-admin-password"))
                .resetKey("outstanding-reset-key")
                .authorities(Set.of())
                .build();
        given(accountRepository.findByLogin("admin")).willReturn(Optional.of(existing));

        initializer.run(null);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());

        assertThat(encoder.matches(PASSWORD, captor.getValue().getPassword())).isTrue();
        assertThat(encoder.matches("the-previous-admin-password", captor.getValue().getPassword())).isFalse();
    }

    @Test
    @DisplayName("a rotation clears any outstanding reset and activation keys")
    void rotationClearsRecoveryKeys() {
        stubAuthorities();
        Account existing = Account.builder()
                .id("acc-1")
                .login("admin")
                .password(encoder.encode("the-previous-admin-password"))
                .resetKey("outstanding-reset-key")
                .activationKey("outstanding-activation-key")
                .authorities(Set.of())
                .build();
        given(accountRepository.findByLogin("admin")).willReturn(Optional.of(existing));

        initializer.run(null);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());

        assertThat(captor.getValue().getResetKey()).isNull();
        assertThat(captor.getValue().getResetDate()).isNull();
        assertThat(captor.getValue().getActivationKey()).isNull();
    }

    @Test
    @DisplayName("writes nothing when the stored password already matches")
    void noWriteWhenPasswordUnchanged() {
        stubAuthorities();
        Account existing = Account.builder()
                .id("acc-1")
                .login("admin")
                .password(encoder.encode(PASSWORD))
                .authorities(Set.of())
                .build();
        given(accountRepository.findByLogin("admin")).willReturn(Optional.of(existing));

        initializer.run(null);

        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("refuses to start when ADMIN_PASSWORD is unset")
    void blankPasswordIsRejected() {
        ReflectionTestUtils.setField(initializer, "adminPassword", "");

        assertThatThrownBy(() -> initializer.run(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ADMIN_PASSWORD");
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("refuses to start when ADMIN_PASSWORD is too short")
    void shortPasswordIsRejected() {
        ReflectionTestUtils.setField(initializer, "adminPassword", "short");

        assertThatThrownBy(() -> initializer.run(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least 12");
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("a custom ADMIN_LOGIN is honoured")
    void honoursCustomLogin() {
        stubAuthorities();
        ReflectionTestUtils.setField(initializer, "adminLogin", "kojo");
        given(accountRepository.findByLogin("kojo")).willReturn(Optional.empty());

        initializer.run(null);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertThat(captor.getValue().getLogin()).isEqualTo("kojo");
    }
}
