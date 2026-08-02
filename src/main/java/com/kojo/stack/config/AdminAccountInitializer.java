package com.kojo.stack.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.kojo.stack.domain.model.Account;
import com.kojo.stack.domain.model.Authority;
import com.kojo.stack.repository.AccountRepository;
import com.kojo.stack.repository.AuthorityRepository;
import com.kojo.stack.security.AuthoritiesConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Creates and maintains the single site administrator account from the environment.
 *
 * The admin credential used to be a literal in three checked-in files
 * (`data.json` in both the API and the deployment stacks, plus a BCrypt hash with the
 * clear text beside it in `mongo-init.sh`). It is now supplied only by
 * {@code ADMIN_PASSWORD} and never stored anywhere in the repository.
 *
 * Behaviour on every start:
 * <ul>
 *   <li>no admin account present - it is created</li>
 *   <li>admin account present with a different password - the password is re-encoded and
 *       updated, which is how the credential is rotated</li>
 *   <li>admin account present with the same password - nothing is written</li>
 * </ul>
 *
 * This runs in <em>all</em> profiles, unlike {@link DataInitConfig}, because production
 * is exactly where the admin account has to exist and be rotatable. It runs after
 * {@code DataInitConfig} so a non-prod seed cannot clobber the admin it creates.
 */
@Component
@Order(AdminAccountInitializer.RUN_ORDER)
@RequiredArgsConstructor
@Slf4j
public class AdminAccountInitializer implements ApplicationRunner {

    /** Runs after the seed data loader, which uses the default (lowest) precedence. */
    static final int RUN_ORDER = Integer.MAX_VALUE;

    private static final int MIN_PASSWORD_LENGTH = 12;

    private final AccountRepository accountRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.login:admin}")
    private String adminLogin;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Value("${app.admin.first-name:Site}")
    private String adminFirstName;

    @Value("${app.admin.last-name:Administrator}")
    private String adminLastName;

    @Override
    public void run(ApplicationArguments args) {
        validatePassword();

        Set<Authority> authorities = resolveAuthorities();

        accountRepository.findByLogin(adminLogin).ifPresentOrElse(
                this::updateExisting,
                () -> createNew(authorities));
    }

    private void validatePassword() {
        if (adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException(
                    "app.admin.password is not configured. Set the ADMIN_PASSWORD environment "
                            + "variable to the site administrator password.");
        }
        if (adminPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalStateException(
                    "app.admin.password is too short; it must be at least "
                            + MIN_PASSWORD_LENGTH + " characters.");
        }
    }

    /**
     * ROLE_ADMIN and ROLE_USER must exist as documents because Account references them
     * by DBRef; create them if the authorities collection has not been seeded.
     */
    private Set<Authority> resolveAuthorities() {
        Set<Authority> authorities = new HashSet<>();
        for (String name : new String[] { AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER }) {
            authorities.add(authorityRepository.findByName(name)
                    .orElseGet(() -> authorityRepository.save(Authority.builder().name(name).build())));
        }
        return authorities;
    }

    private void createNew(Set<Authority> authorities) {
        Account admin = Account.builder()
                .login(adminLogin)
                .email(adminEmail == null || adminEmail.isBlank() ? null : adminEmail)
                .firstName(adminFirstName)
                .lastName(adminLastName)
                .activated(true)
                .langKey("en")
                .password(passwordEncoder.encode(adminPassword))
                .authorities(authorities)
                .build();
        accountRepository.save(admin);
        log.info("Created site administrator account '{}' from the environment", adminLogin);
    }

    private void updateExisting(Account admin) {
        if (passwordEncoder.matches(adminPassword, admin.getPassword())) {
            log.debug("Site administrator account '{}' already matches the configured password", adminLogin);
            return;
        }
        admin.setPassword(passwordEncoder.encode(adminPassword));
        // A rotation invalidates any outstanding recovery tokens.
        admin.setResetKey(null);
        admin.setResetDate(null);
        admin.setActivationKey(null);
        accountRepository.save(admin);
        log.info("Rotated the password for site administrator account '{}'", adminLogin);
    }
}
