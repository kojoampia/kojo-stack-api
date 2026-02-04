package com.kojo.stack.domain.repository;

import com.kojo.stack.domain.model.TechSkill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * TechSkill Repository
 * Data access layer for TechSkill documents in MongoDB
 */
@Repository
public interface SkillRepository extends MongoRepository<TechSkill, String> {

    List<TechSkill> findByCategory(String category);

    Optional<TechSkill> findByName(String name);

    List<TechSkill> findByLevelGreaterThanEqualOrderByLevelDesc(Integer level);
}
