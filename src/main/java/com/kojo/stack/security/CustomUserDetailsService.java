package com.kojo.stack.security;

import com.kojo.stack.domain.model.Account;
import com.kojo.stack.repository.AccountRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * CustomUserDetailsService - Loads user details from MongoDB AccountRepository
 * Implements Spring Security's UserDetailsService for database-backed authentication
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        log.debug("Loading user details for login: {}", login);

        Account account = accountRepository.findByLogin(login)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", login);
                    return new UsernameNotFoundException("User not found: " + login);
                });

        // Convert Authority entities to Spring Security GrantedAuthority
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        if (account.getAuthorities() != null) {
            account.getAuthorities().forEach(auth ->
                    authorities.add(new SimpleGrantedAuthority(auth.getName()))
            );
        }

        log.debug("User found with authorities: {}", authorities);

        return User.builder()
                .username(account.getLogin())
                .password(account.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
