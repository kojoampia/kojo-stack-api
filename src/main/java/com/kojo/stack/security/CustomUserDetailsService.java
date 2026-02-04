package com.kojo.stack.security;

import com.kojo.stack.domain.model.UserLogin;
import com.kojo.stack.domain.repository.UserLoginRepository;
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
 * CustomUserDetailsService - Loads user details from MongoDB UserLoginRepository
 * Implements Spring Security's UserDetailsService for database-backed authentication
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserLoginRepository userLoginRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user details for username: {}", username);

        UserLogin userLogin = userLoginRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        // Convert Authority entities to Spring Security GrantedAuthority
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        if (userLogin.getAuthorities() != null) {
            userLogin.getAuthorities().forEach(auth ->
                    authorities.add(new SimpleGrantedAuthority(auth.getName()))
            );
        }

        log.debug("User found with authorities: {}", authorities);

        return User.builder()
                .username(userLogin.getUsername())
                .password(userLogin.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
