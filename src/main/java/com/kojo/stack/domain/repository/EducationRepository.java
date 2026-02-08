package com.kojo.stack.domain.repository;

import com.kojo.stack.domain.model.Education;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Education Repository
 * Data access layer for Education documents in MongoDB
 */
@Repository
public interface EducationRepository extends MongoRepository<Education, String> {

    List<Education> findByType(String type);

    Optional<Education> findByInstitution(String institution);

    List<Education> findByTypeOrderByDurationDesc(String type);

    List<Education> findAllByOrderByDurationDesc();
}
