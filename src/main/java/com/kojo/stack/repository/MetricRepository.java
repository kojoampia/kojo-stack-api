package com.kojo.stack.repository;

import com.kojo.stack.domain.model.Metric;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MetricRepository - MongoDB repository for Metric documents
 */
@Repository
public interface MetricRepository extends MongoRepository<Metric, String> {

    List<Metric> findByCategory(String category);
}
