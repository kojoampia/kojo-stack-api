package com.kojo.stack.domain.repository;

import com.kojo.stack.domain.model.Experience;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Experience Repository
 * Data access layer for Experience documents in MongoDB
 */
@Repository
public interface ExperienceRepository extends MongoRepository<Experience, String> {

    List<Experience> findByStatus(Experience.StatusType status);

    List<Experience> findByCompanyContainingIgnoreCase(String company);
}
