package com.craftindex.interview.services;

import com.craftindex.interview.models.requests.CreateProjectRequest;
import com.craftindex.interview.models.requests.CreateTaskRequest;
import com.craftindex.interview.models.responses.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface ProjectService {
    ResponseEntity<BaseResponse> createProject(CreateProjectRequest request);
    ResponseEntity<GetProjectsResponse> getProjects(Pageable pageable);
    ResponseEntity<ProjectResponse> getProjectById(long projectId);
    ResponseEntity<BaseResponse> createProjectTask(CreateTaskRequest request, long projectId);
    ResponseEntity<GetProjectTasksResponse> getProjectTasks(long projectId, LocalDate dueDate, String status,Pageable pageable);
    ResponseEntity<BaseResponse> updateTask(CreateTaskRequest request, long taskId);
    ResponseEntity<BaseResponse> deleteTask(long taskId);
    ResponseEntity<List<ProjectSummaryResponse>> getProjectsSummary();
 }
