package com.craftindex.interview.services.impl;

import com.craftindex.interview.entities.ProjectEntity;
import com.craftindex.interview.entities.TaskEntity;
import com.craftindex.interview.enums.Status;
import com.craftindex.interview.models.requests.CreateProjectRequest;
import com.craftindex.interview.models.requests.CreateTaskRequest;
import com.craftindex.interview.models.responses.*;
import com.craftindex.interview.repos.ProjectRepository;
import com.craftindex.interview.repos.TaskRepository;
import com.craftindex.interview.services.ProjectService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class DefaultProjectService implements ProjectService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;


    @Override
    public ResponseEntity<BaseResponse> createProject(CreateProjectRequest request) {
        try {

            BaseResponse baseResponse = new BaseResponse();
            //Check if a project exists with a similar name
            ProjectEntity projectEntity = projectRepository.findByNameIgnoreCase(request.getName()).orElse(null);
            if (projectEntity != null) {
                baseResponse.setMessage("Project with similar name already exists");
                baseResponse.setStatus(HttpStatus.CONFLICT.value());
                return new ResponseEntity<>(baseResponse, HttpStatus.CONFLICT);
            }

            projectEntity = new ProjectEntity();
            projectEntity.setName(request.getName());
            projectEntity.setDescription(request.getDescription());
            projectRepository.save(projectEntity);


            baseResponse.setMessage("Project created successfully");
            baseResponse.setStatus(HttpStatus.OK.value());
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);

        } catch (Exception e) {
            throw new RuntimeException("An error occurred while creating the project", e);

        }


    }

    @Override
    public ResponseEntity<GetProjectsResponse> getProjects(Pageable pageable) {
        try {
            PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("id")));
            Page<ProjectEntity> projects = projectRepository.findAll(pageRequest);
            GetProjectsResponse getProjectsResponse = GetProjectsResponse.builder()
                    .projects(projects.getContent())
                    .build();
            getProjectsResponse.setStatus(HttpStatus.OK.value());
            getProjectsResponse.setMessage("Success");
            return new ResponseEntity<>(getProjectsResponse, HttpStatus.OK);

        } catch (Exception e) {
            throw new RuntimeException("An error occurred while getting the projects", e);
        }

    }

    @Override
    public ResponseEntity<ProjectResponse> getProjectById(long projectId) {
        try {
            ProjectResponse projectResponse = new ProjectResponse();
            ProjectEntity projectEntity = projectRepository.findById(projectId).orElse(null);
            if (projectEntity == null) {
                projectResponse.setMessage("Project with id " + projectId + " not found");
                projectResponse.setStatus(HttpStatus.NOT_FOUND.value());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(projectResponse);
            }

            projectResponse.setProject(projectEntity);
            projectResponse.setStatus(HttpStatus.OK.value());
            projectResponse.setMessage("Success");
            return new ResponseEntity<>(projectResponse, HttpStatus.OK);

        } catch (Exception e) {
            throw new RuntimeException("An error occurred while getting project by id", e);
        }


    }

    @Override
    public ResponseEntity<BaseResponse> createProjectTask(CreateTaskRequest request, long projectId) {
        try {
            //Check if the project exist
            BaseResponse baseResponse = new BaseResponse();
            ProjectEntity projectEntity = projectRepository.findById(projectId).orElse(null);
            if (projectEntity == null) {
                baseResponse.setMessage("Project with id " + projectId + " not found");
                baseResponse.setStatus(HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(baseResponse, HttpStatus.NOT_FOUND);
            }

            //Check if a task with similar name exist
            TaskEntity taskEntity = taskRepository.findByTitleIgnoreCaseAndProjectEntity(request.getTitle(), projectEntity).orElse(null);
            if (taskEntity != null) {
                baseResponse.setMessage("Task with title " + taskEntity.getTitle() + " already exists");
                baseResponse.setStatus(HttpStatus.CONFLICT.value());
                return new ResponseEntity<>(baseResponse, HttpStatus.CONFLICT);
            }

            taskEntity = new TaskEntity();
            taskEntity.setTitle(request.getTitle());
            taskEntity.setDescription(request.getDescription());
            taskEntity.setStatus(request.getStatus());
            taskEntity.setDueDate(request.getDueDate());
            taskEntity.setProjectEntity(projectEntity);
            taskRepository.save(taskEntity);

            baseResponse.setMessage("Task created successfully");
            baseResponse.setStatus(HttpStatus.OK.value());
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);

        } catch (Exception e) {
            throw new RuntimeException("An error occurred while creating the task", e);
        }

    }

    @Override
    public ResponseEntity<GetProjectTasksResponse> getProjectTasks(
            long projectId, LocalDate dueDate, String status, Pageable pageable) {
        try {
            // Check if project exists
            PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("id")));
            GetProjectTasksResponse getProjectTasksResponse = new GetProjectTasksResponse();
            ProjectEntity projectEntity = projectRepository.findById(projectId).orElse(null);
            if (projectEntity == null) {
                getProjectTasksResponse.setMessage("Project with id " + projectId + " not found");
                getProjectTasksResponse.setStatus(HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(getProjectTasksResponse, HttpStatus.NOT_FOUND);
            }

            Page<TaskEntity> tasks;
            if (dueDate == null && status == null) {
                tasks = taskRepository.findByProjectEntity(projectEntity, pageRequest);
            } else if (dueDate != null && status == null) {
                tasks = taskRepository.findByProjectEntityAndDueDate(projectEntity, dueDate, pageRequest);
            } else if (dueDate == null && status != null) {
                tasks = taskRepository.findByProjectEntityAndStatus(projectEntity, status, pageRequest);
            } else {
                tasks = taskRepository.findByProjectEntityAndDueDateAndStatus(projectEntity, dueDate, status, pageRequest);
            }

            getProjectTasksResponse.setStatus(HttpStatus.OK.value());
            getProjectTasksResponse.setMessage("Success");
            getProjectTasksResponse.setTasks(tasks.getContent());
            return new ResponseEntity<>(getProjectTasksResponse, HttpStatus.OK);

        } catch (Exception e) {
            throw new RuntimeException("An error occurred while getting project tasks", e);
        }
    }


    @Override
    public ResponseEntity<BaseResponse> updateTask(CreateTaskRequest request, long taskId) {
        try {
            //Check if the task exist
            BaseResponse baseResponse = new BaseResponse();
            TaskEntity taskEntity = taskRepository.findById(taskId).orElse(null);
            if (taskEntity == null) {
                baseResponse.setMessage("Task with id " + taskId + " not found");
                baseResponse.setStatus(HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(baseResponse, HttpStatus.NOT_FOUND);
            }

            if (request.getTitle() != null
                    && !request.getTitle().isEmpty()
                    && !request.getTitle().equals(taskEntity.getTitle())) {
                taskEntity.setTitle(request.getTitle());
            }

            if (request.getStatus() != null
                    && !request.getDescription().isEmpty()
                    && !request.getDescription().equals(taskEntity.getDescription())) {
                taskEntity.setDescription(request.getDescription());
            }

            if (request.getStatus() != null
                    && !request.getStatus().equals(taskEntity.getStatus())) {
                taskEntity.setStatus(request.getStatus());
            }

            if (request.getDueDate() != null
                    && !request.getDueDate().equals(taskEntity.getDueDate())) {
                taskEntity.setDueDate(request.getDueDate());
            }

            taskRepository.save(taskEntity);
            baseResponse.setMessage("Task updated successfully");
            baseResponse.setStatus(HttpStatus.OK.value());
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);


        }catch (Exception e) {
            throw new RuntimeException("An error occurred while updating the task", e);
        }

    }

    @Override
    public ResponseEntity<BaseResponse> deleteTask(long taskId) {
        try {
            BaseResponse baseResponse = new BaseResponse();
            //Check if the task exist
            TaskEntity taskEntity = taskRepository.findById(taskId).orElse(null);
            if (taskEntity == null) {
                baseResponse.setMessage("Task with id " + taskId + " not found");
                baseResponse.setStatus(HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(baseResponse, HttpStatus.NOT_FOUND);
            }
            taskRepository.delete(taskEntity);
            baseResponse.setMessage("Task with id " + taskId + " successfully deleted");
            baseResponse.setStatus(HttpStatus.OK.value());
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);

        }catch (Exception e) {
            throw new RuntimeException("An error occurred while deleting task", e);
        }

    }

    @Override
    public ResponseEntity<List<ProjectSummaryResponse>> getProjectsSummary() {
        try {

            List<ProjectEntity> projects = projectRepository.findAll();
            List<ProjectSummaryResponse>  projectSummaries = projects.stream()
                    .map(project -> {
                        Map<String, Long> taskStatusCounts = taskRepository
                                .findByProjectEntity(project)
                                .stream()
                                .collect(Collectors.groupingBy(TaskEntity::getStatus, Collectors.counting()));

                        return new ProjectSummaryResponse(project, taskStatusCounts.size());
                    })
                    .toList();

            return new ResponseEntity<>(projectSummaries, HttpStatus.OK);

        }catch (Exception e) {
            throw new RuntimeException("An error occurred while getting projects summary", e);
        }

    }
}
