package com.kojo.stack.domain.repository;

import com.kojo.stack.domain.model.Settings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * SettingsRepository - MongoDB repository for Settings documents
 */
@Repository
public interface SettingsRepository extends MongoRepository<Settings, String> {
}
