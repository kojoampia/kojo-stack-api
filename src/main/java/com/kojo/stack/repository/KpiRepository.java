package com.kojo.stack.repository;

import com.kojo.stack.domain.model.Kpi;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * KpiRepository - MongoDB repository for Kpi documents
 */
@Repository
public interface KpiRepository extends MongoRepository<Kpi, String> {

    List<Kpi> findAllByOrderBySortOrderAsc();
}
