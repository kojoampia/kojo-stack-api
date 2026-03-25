package com.kojo.stack.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kojo.stack.config.Constants;
import com.kojo.stack.domain.User;
import com.kojo.stack.domain.model.Authority;
import com.kojo.stack.repository.AuthorityRepository;
import com.kojo.stack.repository.UserRepository;
import com.kojo.stack.security.AuthoritiesConstants;
import com.kojo.stack.security.SecurityUtils;
import com.kojo.stack.service.dto.AdminUserDTO;
import com.kojo.stack.service.dto.UserDTO;

/**
 * Service class for managing users.
 */
@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
    }

    public Optional<User> activateRegistration(String key) {
        LOG.debug("Activating user for activation key {}", key);
        return userRepository
            .findOneByActivationKey(key)
            .flatMap(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                return saveUser(user);
            })
            .map(user -> {
                LOG.debug("Activated user: {}", user);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        LOG.debug("Reset user password for reset key {}", key);
        return userRepository
            .findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minus(1, ChronoUnit.DAYS)))
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                return user;
            })
            .flatMap(this::saveUser);
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository
            .findOneByEmailIgnoreCase(mail)
            .filter(User::isActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                return user;
            })
            .flatMap(this::saveUser);
    }

    public Optional<User> registerUser(AdminUserDTO userDTO, String password) {
        Optional<User> existingUserByLogin = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
        if (existingUserByLogin.isPresent()) {
            User existingUser = existingUserByLogin.get();
            if (existingUser.isActivated()) {
                throw new UsernameAlreadyUsedException();
            } else {
                userRepository.delete(existingUser);
            }
        }
        
        Optional<User> existingUserByEmail = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUserByEmail.isPresent()) {
            User existingUser = existingUserByEmail.get();
            if (existingUser.isActivated()) {
                throw new EmailAlreadyUsedException();
            } else {
                userRepository.delete(existingUser);
            }
        }
        
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getLogin().toLowerCase());
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey(userDTO.getLangKey());
        newUser.setActivated(false);
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        
        Set<Authority> authorities = new HashSet<>();
        Optional<com.kojo.stack.domain.model.Authority> userAuthority = authorityRepository.findById(AuthoritiesConstants.USER);
        userAuthority.ifPresent(a -> authorities.add(toLegacyAuthority(a)));
        newUser.setAuthorities(authorities);
        
        return saveUser(newUser)
            .map(user -> {
                LOG.debug("Created Information for User: {}", user);
                return user;
            });
    }

    public Optional<User> createUser(AdminUserDTO userDTO) {
        User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        Set<String> authorities = 
        userDTO.getAuthorities() != null ? userDTO.getAuthorities() : new HashSet<>();
        for (String authorityName : authorities) {
            authorityRepository.findById(authorityName).ifPresent(a -> user.getAuthorities().add(toLegacyAuthority(a)));
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generateResetKey());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        return saveUser(user)
            .map(newUser -> {
                newUser.setPassword(encryptedPassword);
                newUser.setResetKey(RandomUtil.generateResetKey());
                newUser.setResetDate(Instant.now());
                newUser.setActivated(true);
                return newUser;
            })
            .flatMap(this::saveUser)
            .map(savedUser -> {
                LOG.debug("Created Information for User: {}", savedUser);
                return savedUser;
            });
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<AdminUserDTO> updateUser(AdminUserDTO userDTO) {
        return userRepository
            .findById(userDTO.getId())
            .flatMap(user -> {
                user.setLogin(userDTO.getLogin().toLowerCase());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                if (userDTO.getEmail() != null) {
                    user.setEmail(userDTO.getEmail().toLowerCase());
                }
                user.setImageUrl(userDTO.getImageUrl());
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());
                Set<Authority> managedAuthorities = user.getAuthorities();
                managedAuthorities.clear();
                for (String authorityName : userDTO.getAuthorities()) {
                    authorityRepository.findById(authorityName).ifPresent(a -> managedAuthorities.add(toLegacyAuthority(a)));
                }
                return Optional.of(user);
            })
            .flatMap(this::saveUser)
            .map(user -> {
                LOG.debug("Changed Information for User: {}", user);
                return new AdminUserDTO(user);
            });
    }

    public Optional<Void> deleteUser(String login) {
        return userRepository
            .findOneByLogin(login)
            .map(user -> {
                userRepository.delete(user);
                LOG.debug("Deleted User: {}", user);
                return null;
            });
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param langKey   language key.
     * @param imageUrl  image URL of user.
     * @return a completed {@link Optional}.
     */
    public Optional<Void> updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .flatMap(user -> {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                if (email != null) {
                    user.setEmail(email.toLowerCase());
                }
                user.setLangKey(langKey);
                user.setImageUrl(imageUrl);
                return saveUser(user);
            })
            .map(user -> {
                LOG.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(u -> null);
    }

    private Optional<User> saveUser(User user) {
        return SecurityUtils.getCurrentUserLogin()
            .or(() -> Optional.of(Constants.SYSTEM))
            .flatMap(login -> {
                if (user.getCreatedBy() == null) {
                    user.setCreatedBy(login);
                }
                user.setLastModifiedBy(login);
                return Optional.of(userRepository.save(user));
            });
    }

    public Optional<Void> changePassword(String currentClearTextPassword, String newPassword) {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .map(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                return user;
            })
            .flatMap(this::saveUser)
            .map(savedUser -> {
                LOG.debug("Changed password for User: {}", savedUser);
                return null;
            });
    }

    public Optional<AdminUserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAllByIdNotNull(pageable).stream().map(AdminUserDTO::new).findFirst();
    }

    public Optional<UserDTO> getAllPublicUsers(Pageable pageable) {
        return userRepository.findAllByIdNotNullAndActivatedIsTrue(pageable).stream().map(UserDTO::new).findFirst();
    }

    public Long countManagedUsers() {
        return userRepository.count();
    }

    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneByLogin(login);
    }

    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired every day, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        removeNotActivatedUsersReactively();
    }

    public void removeNotActivatedUsersReactively() {
        List<User> users = userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
                Instant.now().minus(3, ChronoUnit.DAYS)
            );
        
        users.forEach(user -> {
            userRepository.delete(user);
            LOG.debug("Deleted User: {}", user);
        });
    }

    /**
     * Gets a list of all the authorities.
     * @return a list of all the authorities.
     */
    public List<String> getAuthorities() {
        return (List<String>) authorityRepository.findAll()
        .stream().map(a -> a.getName()).collect(Collectors.toList());
    }

    private Authority toLegacyAuthority(com.kojo.stack.domain.model.Authority authority) {
        Authority legacy = new Authority();
        legacy.setName(authority.getName());
        return legacy;
    }
}
