package com.kojo.stack.service;

import com.kojo.stack.api.dto.AuthorityDTO;
import com.kojo.stack.api.dto.UserLoginDTO;
import com.kojo.stack.domain.model.Authority;
import com.kojo.stack.domain.model.UserLogin;
import com.kojo.stack.domain.repository.AuthorityRepository;
import com.kojo.stack.domain.repository.UserLoginRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * UserLoginService - Business logic for user login management
 * Handles CRUD operations for user credentials and authorities
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserLoginService {

    private final UserLoginRepository userLoginRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @Timed
    @Cacheable(value = "userLogins")
    public List<UserLoginDTO> getAll() {
        log.info("Fetching all user logins");
        return userLoginRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Timed
    @Cacheable(value = "userLogin", key = "#id")
    public UserLoginDTO getById(String id) {
        log.info("Fetching user login with id: {}", id);
        return userLoginRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("User login not found: " + id));
    }

    @Timed
    public UserLoginDTO getByUsername(String username) {
        log.info("Fetching user login by username: {}", username);
        return userLoginRepository.findByUsername(username)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("User login not found for username: " + username));
    }

    @Timed
    public UserLoginDTO getByEmail(String email) {
        log.info("Fetching user login by email: {}", email);
        return userLoginRepository.findByEmail(email)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("User login not found for email: " + email));
    }

    @Timed
    @Transactional
    @CacheEvict(value = "userLogins", allEntries = true)
    public UserLoginDTO create(UserLoginDTO dto) {
        log.info("Creating new user login for username: {}", dto.getUsername());
        
        // Check if username already exists
        if (userLoginRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists: " + dto.getUsername());
        }
        
        // Check if email already exists
        if (userLoginRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + dto.getEmail());
        }
        
        UserLogin entity = toEntity(dto);
        
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
        
        UserLogin saved = userLoginRepository.save(entity);
        return toDTO(saved);
    }

    @Timed
    @Transactional
    @CacheEvict(value = {"userLogins", "userLogin"}, allEntries = true)
    public UserLoginDTO update(String id, UserLoginDTO dto) {
        log.info("Updating user login with id: {}", id);
        
        UserLogin entity = userLoginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User login not found: " + id));
        
        entity.setUsername(dto.getUsername());
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
        
        return toDTO(userLoginRepository.save(entity));
    }

    @Timed
    @Transactional
    @CacheEvict(value = {"userLogins", "userLogin"}, allEntries = true)
    public void delete(String id) {
        log.info("Deleting user login with id: {}", id);
        userLoginRepository.deleteById(id);
    }

    // Mapper methods
    private UserLoginDTO toDTO(UserLogin entity) {
        Set<AuthorityDTO> authorityDTOs = null;
        if (entity.getAuthorities() != null) {
            authorityDTOs = entity.getAuthorities().stream()
                    .map(auth -> AuthorityDTO.builder()
                            .id(auth.getId())
                            .name(auth.getName())
                            .build())
                    .collect(Collectors.toSet());
        }
        
        return UserLoginDTO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .authorities(authorityDTOs)
                .build();
    }

    private UserLogin toEntity(UserLoginDTO dto) {
        return UserLogin.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();
    }
}
