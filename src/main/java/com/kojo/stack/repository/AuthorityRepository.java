package com.kojo.stack.repository;

import com.kojo.stack.domain.model.Authority;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * AuthorityRepository - MongoDB repository for Authority documents
 */
@Repository
public interface AuthorityRepository extends MongoRepository<Authority, String> {

    /**
     * Find authority by name
     */
    Optional<Authority> findByName(String name);
}
