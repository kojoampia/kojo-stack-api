package com.kojo.stack.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.kojo.stack.api.dto.AccountDTO;
import com.kojo.stack.domain.model.Account;
import com.kojo.stack.domain.model.Authority;
import com.kojo.stack.repository.AccountRepository;
import com.kojo.stack.repository.AuthorityRepository;
import com.kojo.stack.security.AuthoritiesConstants;

/**
 * Unit tests for {@link AccountService}.
 *
 * The credential-leak assertions here guard a real exposure: the DTO mapper used to
 * copy the stored BCrypt hash onto every outbound {@code AccountDTO}.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private AccountService accountService;

    private Account storedAccount;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(accountService, "mailFrom", "no-reply@example.com");
        ReflectionTestUtils.setField(accountService, "mailBaseUrl", "http://localhost:4200");

        storedAccount = Account.builder()
                .id("acc-1")
                .login("admin")
                .email("admin@example.com")
                .password("$2a$10$storedbcrypthashvaluehere")
                .activationKey("activation-key")
                .resetKey("reset-key")
                .firstName("Kojo")
                .lastName("Addison")
                .activated(true)
                .langKey("en")
                .authorities(Set.of(Authority.builder().id("auth-1").name(AuthoritiesConstants.ADMIN).build()))
                .build();
    }

    @Test
    @DisplayName("getAll never exposes the stored password hash")
    void getAllDoesNotLeakPassword() {
        given(accountRepository.findAll()).willReturn(List.of(storedAccount));

        List<AccountDTO> result = accountService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPassword()).isNull();
        assertThat(result.get(0).getActivationKey()).isNull();
        assertThat(result.get(0).getResetKey()).isNull();
    }

    @Test
    @DisplayName("getAll still returns the non-sensitive profile fields")
    void getAllReturnsProfileFields() {
        given(accountRepository.findAll()).willReturn(List.of(storedAccount));

        AccountDTO dto = accountService.getAll().get(0);

        assertThat(dto.getLogin()).isEqualTo("admin");
        assertThat(dto.getEmail()).isEqualTo("admin@example.com");
        assertThat(dto.getFirstName()).isEqualTo("Kojo");
        assertThat(dto.isActivated()).isTrue();
        assertThat(dto.getAuthoritiesAsArray()).containsExactly(AuthoritiesConstants.ADMIN);
    }

    @Test
    @DisplayName("getById never exposes the stored password hash")
    void getByIdDoesNotLeakPassword() {
        given(accountRepository.findById("acc-1")).willReturn(Optional.of(storedAccount));

        assertThat(accountService.getById("acc-1").getPassword()).isNull();
    }

    @Test
    @DisplayName("getByLogin never exposes the stored password hash")
    void getByLoginDoesNotLeakPassword() {
        given(accountRepository.findByLogin("admin")).willReturn(Optional.of(storedAccount));

        assertThat(accountService.getByLogin("admin").getPassword()).isNull();
    }

    @Test
    @DisplayName("getByEmail never exposes the stored password hash")
    void getByEmailDoesNotLeakPassword() {
        given(accountRepository.findByEmail("admin@example.com")).willReturn(Optional.of(storedAccount));

        assertThat(accountService.getByEmail("admin@example.com").getPassword()).isNull();
    }

    @Test
    @DisplayName("getById raises when the account is absent")
    void getByIdRaisesWhenMissing() {
        given(accountRepository.findById("nope")).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getById("nope"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("create stores an encoded password, never the clear text")
    void createEncodesPassword() {
        AccountDTO input = AccountDTO.builder()
                .login("newuser")
                .email("new@example.com")
                .password("plaintext-secret")
                .build();

        given(accountRepository.findByLogin("newuser")).willReturn(Optional.empty());
        given(accountRepository.findByEmail("new@example.com")).willReturn(Optional.empty());
        given(passwordEncoder.encode("plaintext-secret")).willReturn("encoded-value");
        given(accountRepository.save(any(Account.class))).willAnswer(inv -> inv.getArgument(0));

        AccountDTO created = accountService.create(input);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("encoded-value");
        assertThat(captor.getValue().getPassword()).isNotEqualTo("plaintext-secret");
        assertThat(created.getPassword()).isNull();
    }

    @Test
    @DisplayName("create rejects a duplicate login")
    void createRejectsDuplicateLogin() {
        given(accountRepository.findByLogin("admin")).willReturn(Optional.of(storedAccount));

        AccountDTO input = AccountDTO.builder().login("admin").email("other@example.com").build();

        assertThatThrownBy(() -> accountService.create(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Login already exists");
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("create rejects a duplicate email")
    void createRejectsDuplicateEmail() {
        given(accountRepository.findByLogin("newuser")).willReturn(Optional.empty());
        given(accountRepository.findByEmail("admin@example.com")).willReturn(Optional.of(storedAccount));

        AccountDTO input = AccountDTO.builder().login("newuser").email("admin@example.com").build();

        assertThatThrownBy(() -> accountService.create(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already exists");
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("update leaves the existing password untouched when none is supplied")
    void updateKeepsExistingPasswordWhenBlank() {
        given(accountRepository.findById("acc-1")).willReturn(Optional.of(storedAccount));
        given(accountRepository.save(any(Account.class))).willAnswer(inv -> inv.getArgument(0));

        AccountDTO input = AccountDTO.builder()
                .login("admin")
                .email("admin@example.com")
                .password("")
                .build();

        accountService.update("acc-1", input);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("$2a$10$storedbcrypthashvaluehere");
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("update encodes a newly supplied password")
    void updateEncodesNewPassword() {
        given(accountRepository.findById("acc-1")).willReturn(Optional.of(storedAccount));
        given(passwordEncoder.encode("brand-new-password")).willReturn("newly-encoded");
        given(accountRepository.save(any(Account.class))).willAnswer(inv -> inv.getArgument(0));

        AccountDTO input = AccountDTO.builder()
                .login("admin")
                .email("admin@example.com")
                .password("brand-new-password")
                .build();

        accountService.update("acc-1", input);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("newly-encoded");
    }

    @Test
    @DisplayName("delete removes the account by id")
    void deleteRemovesAccount() {
        accountService.delete("acc-1");

        verify(accountRepository).deleteById("acc-1");
    }
}
