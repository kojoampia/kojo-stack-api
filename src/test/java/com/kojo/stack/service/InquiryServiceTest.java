package com.kojo.stack.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kojo.stack.api.dto.InquiryDTO;
import com.kojo.stack.api.mapper.InquiryMapper;
import com.kojo.stack.domain.model.Inquiry;
import com.kojo.stack.repository.InquiryRepository;

/**
 * Unit tests for {@link InquiryService}.
 */
@ExtendWith(MockitoExtension.class)
class InquiryServiceTest {

    @Mock
    private InquiryRepository repository;

    @Mock
    private InquiryMapper mapper;

    @InjectMocks
    private InquiryService inquiryService;

    private Inquiry entity;
    private InquiryDTO dto;

    @BeforeEach
    void setUp() {
        entity = Inquiry.builder()
                .id("i-1")
                .name("Jane Client")
                .email("jane@example.com")
                .type(Inquiry.InquiryType.CONSULTING)
                .status(Inquiry.InquiryStatus.NEW)
                .message("Please get in touch.")
                .build();

        dto = InquiryDTO.builder()
                .id("i-1")
                .name("Jane Client")
                .email("jane@example.com")
                .type("CONSULTING")
                .status("NEW")
                .message("Please get in touch.")
                .build();
    }

    @Test
    @DisplayName("getAll maps every inquiry")
    void getAllMapsEntities() {
        given(repository.findAll()).willReturn(List.of(entity));
        given(mapper.toDTO(entity)).willReturn(dto);

        assertThat(inquiryService.getAll()).containsExactly(dto);
    }

    @Test
    @DisplayName("getNewInquiries queries only NEW inquiries")
    void getNewFiltersByStatus() {
        given(repository.findByStatus(Inquiry.InquiryStatus.NEW)).willReturn(List.of(entity));
        given(mapper.toDTO(entity)).willReturn(dto);

        assertThat(inquiryService.getNewInquiries()).containsExactly(dto);
        verify(repository).findByStatus(Inquiry.InquiryStatus.NEW);
    }

    @Test
    @DisplayName("getByType returns empty for an unknown type instead of raising")
    void getByTypeSwallowsUnknownType() {
        assertThat(inquiryService.getByType("NOPE")).isEmpty();
        verify(repository, never()).findByType(any());
    }

    @Test
    @DisplayName("a submitted inquiry always starts in the NEW state")
    void submitForcesNewStatus() {
        InquiryDTO submitted = InquiryDTO.builder()
                .name("Jane Client")
                .email("jane@example.com")
                .type("CONSULTING")
                // a caller attempting to pre-set a privileged status must not win
                .status("COMPLETED")
                .build();

        given(mapper.toEntity(submitted)).willReturn(Inquiry.builder().name("Jane Client").build());
        given(repository.save(any(Inquiry.class))).willAnswer(inv -> inv.getArgument(0));
        given(mapper.toDTO(any(Inquiry.class))).willReturn(dto);

        inquiryService.submitInquiry(submitted);

        ArgumentCaptor<Inquiry> captor = ArgumentCaptor.forClass(Inquiry.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(Inquiry.InquiryStatus.NEW);
    }

    @Test
    @DisplayName("update raises when the inquiry is absent")
    void updateRaisesWhenMissing() {
        given(repository.findById("missing")).willReturn(Optional.empty());

        assertThatThrownBy(() -> inquiryService.update("missing", dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Inquiry not found");
    }

    @Test
    @DisplayName("update rejects an invalid type")
    void updateRejectsInvalidType() {
        given(repository.findById("i-1")).willReturn(Optional.of(entity));

        InquiryDTO invalid = InquiryDTO.builder()
                .name("Jane")
                .email("jane@example.com")
                .type("NOT_A_TYPE")
                .build();

        assertThatThrownBy(() -> inquiryService.update("i-1", invalid))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid type");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update rejects an invalid status")
    void updateRejectsInvalidStatus() {
        given(repository.findById("i-1")).willReturn(Optional.of(entity));

        InquiryDTO invalid = InquiryDTO.builder()
                .name("Jane")
                .email("jane@example.com")
                .status("NOT_A_STATUS")
                .build();

        assertThatThrownBy(() -> inquiryService.update("i-1", invalid))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid status");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("updateStatus writes a valid status")
    void updateStatusAppliesValidStatus() {
        given(repository.findById("i-1")).willReturn(Optional.of(entity));
        given(repository.save(any(Inquiry.class))).willAnswer(inv -> inv.getArgument(0));
        given(mapper.toDTO(any(Inquiry.class))).willReturn(dto);

        inquiryService.updateStatus("i-1", "CONTACTED");

        ArgumentCaptor<Inquiry> captor = ArgumentCaptor.forClass(Inquiry.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(Inquiry.InquiryStatus.CONTACTED);
    }

    @Test
    @DisplayName("updateStatus rejects an invalid status")
    void updateStatusRejectsInvalidStatus() {
        given(repository.findById("i-1")).willReturn(Optional.of(entity));

        assertThatThrownBy(() -> inquiryService.updateStatus("i-1", "BOGUS"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid status");
    }

    @Test
    @DisplayName("deleteInquiry removes the inquiry by id")
    void deleteRemovesInquiry() {
        inquiryService.deleteInquiry("i-1");

        verify(repository).deleteById("i-1");
    }
}
