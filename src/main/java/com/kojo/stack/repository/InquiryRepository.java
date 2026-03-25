package com.kojo.stack.repository;

import com.kojo.stack.domain.model.Inquiry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Inquiry Repository
 * Data access layer for Inquiry documents in MongoDB
 */
@Repository
public interface InquiryRepository extends MongoRepository<Inquiry, String> {

    List<Inquiry> findByStatus(Inquiry.InquiryStatus status);

    List<Inquiry> findByType(Inquiry.InquiryType type);

    List<Inquiry> findByEmailContainingIgnoreCase(String email);
}
