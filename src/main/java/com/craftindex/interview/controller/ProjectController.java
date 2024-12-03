package com.craftindex.interview.controller;

import com.craftindex.interview.models.requests.CreateProjectRequest;
import com.craftindex.interview.models.requests.CreateTaskRequest;
import com.craftindex.interview.models.responses.*;
import com.craftindex.interview.services.ProjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/projects")
    private ResponseEntity<BaseResponse> createCustomer(@RequestBody @Valid CreateProjectRequest request){
        return projectService.createProject(request);
    }

    @GetMapping("/projects")
    private ResponseEntity<GetProjectsResponse> createCustomer(@PageableDefault(page = 0, size = 5) Pageable pageable){
        return projectService.getProjects(pageable);
    }

    @GetMapping("/projects/{projectId}")
    private ResponseEntity<ProjectResponse> getProjectById(@PathVariable("projectId") Long projectId){
        return projectService.getProjectById(projectId);
    }

    @PostMapping("/projects/{projectId}/tasks")
    private ResponseEntity<BaseResponse> createTask(@RequestBody @Valid CreateTaskRequest request, @PathVariable("projectId") long projectId){
        return projectService.createProjectTask(request, projectId);
    }

    @GetMapping("/projects/{projectId}/tasks")
    private ResponseEntity<GetProjectTasksResponse> getProjectTasks(
            @PathVariable("projectId") long projectId,
            @RequestParam(value = "dueDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(value = "status", required = false) String status,
            @PageableDefault(page = 0, size = 5) Pageable pageable) {
        return projectService.getProjectTasks(projectId, dueDate, status, pageable);
    }


    @PutMapping("/tasks/{taskId}")
    private ResponseEntity<BaseResponse> updateTask(@RequestBody @Valid CreateTaskRequest request,@PathVariable("taskId") long taskId){
        return projectService.updateTask(request, taskId);
    }


    @DeleteMapping("/tasks/{taskId}")
    private ResponseEntity<BaseResponse> deleteTask(@PathVariable("taskId") long taskId){
        return projectService.deleteTask(taskId);
    }

    @GetMapping("/projects/summary")
    public ResponseEntity<List<ProjectSummaryResponse>> getProjectSummary() {
        return projectService.getProjectsSummary();
    }


}
