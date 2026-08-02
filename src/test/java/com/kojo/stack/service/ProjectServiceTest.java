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

import com.kojo.stack.api.dto.ProjectDTO;
import com.kojo.stack.api.mapper.ProjectMapper;
import com.kojo.stack.domain.model.Project;
import com.kojo.stack.repository.ProjectRepository;

/**
 * Unit tests for {@link ProjectService}.
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository repository;

    @Mock
    private ProjectMapper mapper;

    @InjectMocks
    private ProjectService projectService;

    private Project entity;
    private ProjectDTO dto;

    @BeforeEach
    void setUp() {
        entity = Project.builder()
                .id("p-1")
                .name("Command Center")
                .client("Acme")
                .type(Project.ProjectType.ARCHITECTURE)
                .status(Project.ProjectStatus.ACTIVE)
                .build();

        dto = ProjectDTO.builder()
                .id("p-1")
                .name("Command Center")
                .client("Acme")
                .type("ARCHITECTURE")
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("getAll maps every entity through the mapper")
    void getAllMapsEntities() {
        given(repository.findAll()).willReturn(List.of(entity));
        given(mapper.toDTO(entity)).willReturn(dto);

        assertThat(projectService.getAll()).containsExactly(dto);
    }

    @Test
    @DisplayName("getActive queries only ACTIVE projects")
    void getActiveFiltersByStatus() {
        given(repository.findByStatus(Project.ProjectStatus.ACTIVE)).willReturn(List.of(entity));
        given(mapper.toDTO(entity)).willReturn(dto);

        assertThat(projectService.getActive()).containsExactly(dto);
        verify(repository).findByStatus(Project.ProjectStatus.ACTIVE);
    }

    @Test
    @DisplayName("getByType resolves a valid type name")
    void getByTypeResolvesValidType() {
        given(repository.findByType(Project.ProjectType.DEVOPS)).willReturn(List.of(entity));
        given(mapper.toDTO(entity)).willReturn(dto);

        assertThat(projectService.getByType("DEVOPS")).containsExactly(dto);
    }

    @Test
    @DisplayName("getByType returns empty for an unknown type instead of raising")
    void getByTypeSwallowsUnknownType() {
        assertThat(projectService.getByType("NOT_A_REAL_TYPE")).isEmpty();
        verify(repository, never()).findByType(any());
    }

    @Test
    @DisplayName("searchByClient delegates a case-insensitive contains query")
    void searchByClientDelegates() {
        given(repository.findByClientContainingIgnoreCase("acme")).willReturn(List.of(entity));
        given(mapper.toDTO(entity)).willReturn(dto);

        assertThat(projectService.searchByClient("acme")).containsExactly(dto);
    }

    @Test
    @DisplayName("create converts the string status and type onto the entity")
    void createConvertsEnums() {
        ProjectDTO input = ProjectDTO.builder()
                .name("New")
                .type("MICROSERVICES")
                .status("PLANNING")
                .build();

        given(mapper.toEntity(input)).willReturn(Project.builder().name("New").build());
        given(repository.save(any(Project.class))).willAnswer(inv -> inv.getArgument(0));
        given(mapper.toDTO(any(Project.class))).willReturn(input);

        projectService.create(input);

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getType()).isEqualTo(Project.ProjectType.MICROSERVICES);
        assertThat(captor.getValue().getStatus()).isEqualTo(Project.ProjectStatus.PLANNING);
    }

    @Test
    @DisplayName("update raises when the project is absent")
    void updateRaisesWhenMissing() {
        given(repository.findById("missing")).willReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.update("missing", dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Project not found");
    }

    @Test
    @DisplayName("update writes the supplied fields onto the stored entity")
    void updateAppliesFields() {
        given(repository.findById("p-1")).willReturn(Optional.of(entity));
        given(repository.save(any(Project.class))).willAnswer(inv -> inv.getArgument(0));
        given(mapper.toDTO(any(Project.class))).willReturn(dto);

        ProjectDTO changes = ProjectDTO.builder()
                .name("Renamed")
                .client("Globex")
                .type("CONSULTING")
                .status("COMPLETED")
                .description("updated description")
                .build();

        projectService.update("p-1", changes);

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Renamed");
        assertThat(captor.getValue().getClient()).isEqualTo("Globex");
        assertThat(captor.getValue().getType()).isEqualTo(Project.ProjectType.CONSULTING);
        assertThat(captor.getValue().getStatus()).isEqualTo(Project.ProjectStatus.COMPLETED);
        assertThat(captor.getValue().getDescription()).isEqualTo("updated description");
    }

    @Test
    @DisplayName("delete removes the project by id")
    void deleteRemovesProject() {
        projectService.delete("p-1");

        verify(repository).deleteById("p-1");
    }
}
