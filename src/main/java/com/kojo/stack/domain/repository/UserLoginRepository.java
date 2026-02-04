package com.kojo.stack.domain.repository;

import com.kojo.stack.domain.model.UserLogin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserLoginRepository - MongoDB repository for UserLogin documents
 */
@Repository
public interface UserLoginRepository extends MongoRepository<UserLogin, String> {

    /**
     * Find user login by username
     */
    Optional<UserLogin> findByUsername(String username);

    /**
     * Find user login by email
     */
    Optional<UserLogin> findByEmail(String email);
}
