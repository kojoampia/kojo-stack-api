package com.kojo.stack.repository;

import com.kojo.stack.domain.model.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ProfileRepository - MongoDB repository for Profile documents
 */
@Repository
public interface ProfileRepository extends MongoRepository<Profile, String> {
    Optional<Profile> findByEmail(String email);
}
