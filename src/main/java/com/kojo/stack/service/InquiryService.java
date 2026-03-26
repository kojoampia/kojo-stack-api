package com.kojo.stack.service;

import com.kojo.stack.api.dto.InquiryDTO;
import com.kojo.stack.api.mapper.InquiryMapper;
import com.kojo.stack.domain.model.Inquiry;
import com.kojo.stack.repository.InquiryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * InquiryService - Business logic for inquiry management
 * Handles the "Hire Consultant" form submissions
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository repository;
    private final InquiryMapper mapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public List<InquiryDTO> getAll() {
        log.info("Fetching all inquiries");
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<InquiryDTO> getNewInquiries() {
        log.info("Fetching new inquiries");
        return repository.findByStatus(Inquiry.InquiryStatus.NEW).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<InquiryDTO> getByType(String type) {
        log.info("Fetching inquiries by type: {}", type);
        try {
            Inquiry.InquiryType inquiryType = Inquiry.InquiryType.valueOf(type);
            return repository.findByType(inquiryType).stream()
                    .map(mapper::toDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid inquiry type: {}", type);
            return List.of();
        }
    }

    @Transactional
    public InquiryDTO submitInquiry(InquiryDTO dto) {
        log.info("Processing new inquiry from: {}", dto.getEmail());
        
        Inquiry entity = mapper.toEntity(dto);
        entity.setStatus(Inquiry.InquiryStatus.NEW);
        
        Inquiry saved = repository.save(entity);
        
        // Publish event for email notifications, etc.
        // publishInquiryReceivedEvent(saved);
        
        return mapper.toDTO(saved);
    }

    @Transactional
    public InquiryDTO update(String id, InquiryDTO dto) {
        log.info("Updating inquiry with id: {}", id);

        Inquiry entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inquiry not found: " + id));

        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setMessage(dto.getMessage());

        if (dto.getType() != null) {
            try {
                entity.setType(Inquiry.InquiryType.valueOf(dto.getType()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid type: " + dto.getType());
            }
        }
        if (dto.getStatus() != null) {
            try {
                entity.setStatus(Inquiry.InquiryStatus.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid status: " + dto.getStatus());
            }
        }

        return mapper.toDTO(repository.save(entity));
    }

    @Transactional
    public InquiryDTO updateStatus(String id, String status) {
        log.info("Updating inquiry {} status to: {}", id, status);
        
        Inquiry entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inquiry not found: " + id));
        
        try {
            entity.setStatus(Inquiry.InquiryStatus.valueOf(status));
            return mapper.toDTO(repository.save(entity));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }

    @Transactional
    public void deleteInquiry(String id) {
        log.info("Deleting inquiry with id: {}", id);
        repository.deleteById(id);
    }

    private void publishInquiryReceivedEvent(Inquiry inquiry) {
        try {
            kafkaTemplate.send("inquiry-events", "inquiry.received", inquiry);
            log.info("Published inquiry.received event for inquiry: {}", inquiry.getId());
        } catch (Exception e) {
            log.warn("Failed to publish event for inquiry: {}", inquiry.getId(), e);
        }
    }
}
