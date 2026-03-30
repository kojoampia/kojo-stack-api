package com.kojo.stack.service;

import com.kojo.stack.api.dto.AuthorityDTO;
import com.kojo.stack.api.dto.AccountDTO;
import com.kojo.stack.domain.model.Authority;
import com.kojo.stack.repository.AccountRepository;
import com.kojo.stack.repository.AuthorityRepository;
import com.kojo.stack.domain.model.Account;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.kojo.stack.security.SecurityUtils;

/**
 * AccountService - Business logic for user login management
 * Handles CRUD operations for user credentials and authorities
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @Timed
    @Cacheable(value = "Accounts")
    public List<AccountDTO> getAll() {
        log.info("Fetching all user logins");
        return accountRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Timed
    @Cacheable(value = "Account", key = "#id")
    public AccountDTO getById(String id) {
        log.info("Fetching user login with id: {}", id);
        return accountRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("User login not found: " + id));
    }

    @Timed
    public AccountDTO getByLogin(String login) {
        log.info("Fetching user login by login: {}", login);
        return accountRepository.findByLogin(login)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("User login not found for login: " + login));
    }

    @Timed
    public AccountDTO getByEmail(String email) {
        log.info("Fetching user login by email: {}", email);
        return accountRepository.findByEmail(email)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("User login not found for email: " + email));
    }

    @Timed
    @Transactional
    @CacheEvict(value = "Accounts", allEntries = true)
    public AccountDTO create(AccountDTO dto) {
        log.info("Creating new user login for login: {}", dto.getLogin());
        
        // Check if login already exists
        if (accountRepository.findByLogin(dto.getLogin()).isPresent()) {
            throw new RuntimeException("Login already exists: " + dto.getLogin());
        }
        
        // Check if email already exists
        if (accountRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + dto.getEmail());
        }
        
        Account entity = toEntity(dto);
        
        // Encode password
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        // Resolve authority references
        if (dto.getAuthorities() != null) {
            Set<Authority> authorities = dto.getAuthorities().stream()
                    .map(authDto -> authorityRepository.findByName(authDto.getName())
                            .orElseGet(() -> {
                                Authority authority = Authority.builder()
                                        .name(authDto.getName())
                                        .build();
                                return authorityRepository.save(authority);
                            }))
                    .collect(Collectors.toSet());
            entity.setAuthorities(authorities);
        }
        
        Account saved = accountRepository.save(entity);
        return toDTO(saved);
    }

    @Timed
    @Transactional
    @CacheEvict(value = {"Accounts", "Account"}, allEntries = true)
    public AccountDTO update(String id, AccountDTO dto) {
        log.info("Updating user login with id: {}", id);
        
        Account entity = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User login not found: " + id));
        
        entity.setLogin(dto.getLogin());
        entity.setEmail(dto.getEmail());
        
        // Only update password if provided
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        
        // Update authorities if provided
        if (dto.getAuthorities() != null) {
            Set<Authority> authorities = dto.getAuthorities().stream()
                    .map(authDto -> authorityRepository.findByName(authDto.getName())
                            .orElseGet(() -> {
                                Authority authority = Authority.builder()
                                        .name(authDto.getName())
                                        .build();
                                return authorityRepository.save(authority);
                            }))
                    .collect(Collectors.toSet());
            entity.setAuthorities(authorities);
        }
        
        return toDTO(accountRepository.save(entity));
    }

    @Timed
    @Transactional
    @CacheEvict(value = {"Accounts", "Account"}, allEntries = true)
    public void delete(String id) {
        log.info("Deleting user login with id: {}", id);
        accountRepository.deleteById(id);
    }

    // Mapper methods
    private AccountDTO toDTO(Account entity) {
        Set<AuthorityDTO> authorityDTOs = null;
        if (entity.getAuthorities() != null) {
            authorityDTOs = entity.getAuthorities().stream()
                    .map(auth -> AuthorityDTO.builder()
                            .id(auth.getId())
                            .name(auth.getName())
                            .build())
                    .collect(Collectors.toSet());
        }
        
        return AccountDTO.builder()
                .id(entity.getId())
                .login(entity.getLogin())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .authorities(authorityDTOs)
                .activated(entity.isActivated())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .langKey(entity.getLangKey())
                .imageUrl(entity.getImageUrl())
                .build();
    }

    private Account toEntity(AccountDTO dto) {
        return Account.builder()
                .id(dto.getId())
                .login(dto.getLogin())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .activated(dto.isActivated())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .langKey(dto.getLangKey())
                .imageUrl(dto.getImageUrl())
                .build();
    }
    public Optional<Void> changePassword(String currentClearTextPassword, String newPassword) {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(accountRepository::findOneByLogin)
            .map(account -> {
                String currentEncryptedPassword = account.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                account.setPassword(passwordEncoder.encode(newPassword));
                return account;
            })
            .flatMap(this::saveAccount)
            .map(savedAccount -> {
                log.debug("Changed password for Account: {}", savedAccount.getLogin());
                return null;
            });
    }

    private Optional<Account> saveAccount(Account account) {
        return Optional.of(accountRepository.save(account));
    }
}
