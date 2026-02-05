package com.kojo.stack.domain.repository;

import com.kojo.stack.domain.model.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * AccountRepository - MongoDB repository for Account documents
 */
@Repository
public interface AccountRepository extends MongoRepository<Account, String> {

    /**
     * Find user login by login
     */
    Optional<Account> findByLogin(String login);

    /**
     * Find user login by email
     */
    Optional<Account> findByEmail(String email);

    Optional<Account> findByResetKey(String resetKey);

    Optional<Account> findByActivationKey(String activationKey);

    Optional<Account> findByFirstName(String firstName);

    Optional<Account> findByLastName(String lastName);

    
}
