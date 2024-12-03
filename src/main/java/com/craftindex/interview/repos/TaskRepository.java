package com.craftindex.interview.repos;

import com.craftindex.interview.entities.ProjectEntity;
import com.craftindex.interview.entities.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    Optional<TaskEntity> findByTitleIgnoreCaseAndProjectEntity(String title, ProjectEntity projectEntity);
    Page<TaskEntity> findByProjectEntity(ProjectEntity projectEntity, Pageable pageable);
    Page<TaskEntity> findByProjectEntityAndDueDate(ProjectEntity projectEntity, LocalDate dueDate, Pageable pageable);
    Page<TaskEntity> findByProjectEntityAndStatus(ProjectEntity projectEntity, String status, Pageable pageable);
    Page<TaskEntity> findByProjectEntityAndDueDateAndStatus(ProjectEntity projectEntity, LocalDate dueDate, String status, Pageable pageable);

    @Query("SELECT t FROM TaskEntity t WHERE t.projectEntity = :projectEntity")
    List<TaskEntity> findByProjectEntity(@Param("projectEntity") ProjectEntity projectEntity);

}
