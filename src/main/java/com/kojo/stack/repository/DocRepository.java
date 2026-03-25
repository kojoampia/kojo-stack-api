package com.kojo.stack.repository;

import com.kojo.stack.domain.model.Doc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Documentation Repository
 * Data access layer for Doc documents in MongoDB
 */
@Repository
public interface DocRepository extends MongoRepository<Doc, String> {

    List<Doc> findByType(String type);

    List<Doc> findByTitleContainingIgnoreCase(String title);

    @Query("{ 'tags': ?0 }")
    List<Doc> findByTag(String tag);
}
