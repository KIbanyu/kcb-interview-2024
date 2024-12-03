package com.craftindex.interview;


import com.craftindex.interview.entities.ProjectEntity;
import com.craftindex.interview.entities.TaskEntity;
import com.craftindex.interview.enums.Status;
import com.craftindex.interview.models.requests.CreateProjectRequest;
import com.craftindex.interview.models.requests.CreateTaskRequest;
import com.craftindex.interview.models.responses.BaseResponse;
import com.craftindex.interview.repos.ProjectRepository;
import com.craftindex.interview.repos.TaskRepository;
import com.craftindex.interview.services.ProjectService;
import com.craftindex.interview.services.impl.DefaultProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InterviewApplicationTests {

	@Mock
	private ProjectRepository projectRepository;

	@Mock
	private TaskRepository taskRepository;

	@InjectMocks
	private DefaultProjectService projectService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void createProjectSuccess() {
		CreateProjectRequest request = new CreateProjectRequest();
		request.setName("New Project");
		request.setDescription("Description of the project");
		when(projectRepository.findByNameIgnoreCase("New Project")).thenReturn(Optional.empty());
		ResponseEntity<BaseResponse> response = projectService.createProject(request);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Project created successfully", response.getBody().getMessage());
		verify(projectRepository, times(1)).save(any(ProjectEntity.class));
	}

	@Test
	void createProjectConflict() {
		CreateProjectRequest request = new CreateProjectRequest();
		request.setName("Existing Project");
		ProjectEntity existingProject = new ProjectEntity();
		existingProject.setName("Existing Project");
		when(projectRepository.findByNameIgnoreCase("Existing Project")).thenReturn(Optional.of(existingProject));
		ResponseEntity<BaseResponse> response = projectService.createProject(request);
		assertNotNull(response);
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertEquals("Project with similar name already exists", response.getBody().getMessage());
		verify(projectRepository, never()).save(any(ProjectEntity.class));
	}

	@Test
	void getProjectsSuccess() {
		PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("id")));
		ProjectEntity projectEntity = new ProjectEntity();
		projectEntity.setName("Project 1");
		Page<ProjectEntity> projectPage = new PageImpl<>(Collections.singletonList(projectEntity));
		when(projectRepository.findAll(pageRequest)).thenReturn(projectPage);
		ResponseEntity<?> response = projectService.getProjects(pageRequest);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(projectRepository, times(1)).findAll(pageRequest);
	}

	@Test
	void createProjectTaskProjectNotFound() {
		CreateTaskRequest request = new CreateTaskRequest();
		request.setTitle("Task 1");
		when(projectRepository.findById(1L)).thenReturn(Optional.empty());
		ResponseEntity<BaseResponse> response = projectService.createProjectTask(request, 1L);
		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Project with id 1 not found", response.getBody().getMessage());
		verify(taskRepository, never()).save(any(TaskEntity.class));
	}

	@Test
	void createProjectTaskSuccess() {
		CreateTaskRequest request = new CreateTaskRequest();
		request.setTitle("Task 1");
		request.setDescription("Task description");
		request.setDueDate(LocalDate.now());
		request.setStatus("TO_DO");
		ProjectEntity projectEntity = new ProjectEntity();
		projectEntity.setId(1L);
		when(projectRepository.findById(1L)).thenReturn(Optional.of(projectEntity));
		when(taskRepository.findByTitleIgnoreCaseAndProjectEntity("Task 1", projectEntity)).thenReturn(Optional.empty());
		ResponseEntity<BaseResponse> response = projectService.createProjectTask(request, 1L);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Task created successfully", response.getBody().getMessage());
		verify(taskRepository, times(1)).save(any(TaskEntity.class));
	}

	@Test
	void deleteTaskSuccess() {
		TaskEntity taskEntity = new TaskEntity();
		taskEntity.setId(1L);
		when(taskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));
		ResponseEntity<BaseResponse> response = projectService.deleteTask(1L);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Task with id 1 successfully deleted", response.getBody().getMessage());
		verify(taskRepository, times(1)).delete(taskEntity);
	}

	@Test
	void deleteTaskNotFound() {
		when(taskRepository.findById(1L)).thenReturn(Optional.empty());
		ResponseEntity<BaseResponse> response = projectService.deleteTask(1L);
		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Task with id 1 not found", response.getBody().getMessage());
		verify(taskRepository, never()).delete(any(TaskEntity.class));
	}

	@Test
	void updateTaskTaskDoesNotExistReturnsNotFound() {
		long taskId = 1L;
		CreateTaskRequest request = new CreateTaskRequest();
		request.setTitle("Updated Title");
		Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
		ResponseEntity<BaseResponse> response = projectService.updateTask(request, taskId);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("Task with id 1 not found", response.getBody().getMessage());
	}


	@Test
	void updateTaskValidUpdateReturnsOk() {
		long taskId = 1L;
		CreateTaskRequest request = new CreateTaskRequest();
		request.setTitle("Updated Title");
		request.setDescription("Updated Description");
		request.setStatus("IN_PROGRESS");
		request.setDueDate(LocalDate.now().plusDays(5));
		TaskEntity existingTask = new TaskEntity();
		existingTask.setId(taskId);
		existingTask.setTitle("Old Title");
		existingTask.setDescription("Old Description");
		existingTask.setStatus("TO_DO");
		existingTask.setDueDate(LocalDate.now());
		Mockito.when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
		ResponseEntity<BaseResponse> response = projectService.updateTask(request, taskId);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("Task updated successfully", response.getBody().getMessage());
		assertEquals("Updated Title", existingTask.getTitle());
		assertEquals("Updated Description", existingTask.getDescription());
		assertEquals("IN_PROGRESS", existingTask.getStatus());
		assertEquals(LocalDate.now().plusDays(5), existingTask.getDueDate());
		Mockito.verify(taskRepository, Mockito.times(1)).save(existingTask);
	}

}
