package com.craftindex.interview;


import com.craftindex.interview.controller.ProjectController;
import com.craftindex.interview.models.requests.CreateProjectRequest;
import com.craftindex.interview.models.requests.CreateTaskRequest;
import com.craftindex.interview.models.responses.BaseResponse;
import com.craftindex.interview.models.responses.GetProjectsResponse;
import com.craftindex.interview.models.responses.ProjectResponse;
import com.craftindex.interview.services.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
    }

    @Test
    public void testCreateProject() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Project Name");
        request.setDescription("Project Description");

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage("Project created successfully");
        baseResponse.setStatus(HttpStatus.OK.value());

        when(projectService.createProject(any(CreateProjectRequest.class))).thenReturn(new ResponseEntity<>(baseResponse, HttpStatus.OK));

        mockMvc.perform(post("/api/v1/projects")
                        .contentType("application/json")
                        .content("{\"name\":\"Project Name\", \"description\":\"Project Description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Project created successfully"))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()));

        verify(projectService, times(1)).createProject(any(CreateProjectRequest.class));
    }

    @Test
    public void testGetProjectById() throws Exception {
        Long projectId = 1L;
        ProjectResponse projectResponse = new ProjectResponse();
        projectResponse.setMessage("Success");
        projectResponse.setStatus(HttpStatus.OK.value());

        when(projectService.getProjectById(projectId)).thenReturn(new ResponseEntity<>(projectResponse, HttpStatus.OK));

        mockMvc.perform(get("/api/v1/projects/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()));

        verify(projectService, times(1)).getProjectById(projectId);
    }

    @Test
    public void testCreateTask() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Task Title");
        request.setDescription("Task Description");

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage("Task created successfully");
        baseResponse.setStatus(HttpStatus.OK.value());

        when(projectService.createProjectTask(any(CreateTaskRequest.class), eq(1L)))
                .thenReturn(new ResponseEntity<>(baseResponse, HttpStatus.OK));

        mockMvc.perform(post("/api/v1/projects/{projectId}/tasks", 1L)
                        .contentType("application/json")
                        .content("{\"title\":\"Task Title\", \"description\":\"Task Description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Task created successfully"))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()));

        verify(projectService, times(1)).createProjectTask(any(CreateTaskRequest.class), eq(1L));
    }

    @Test
    public void testUpdateTask() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Updated Task Title");
        request.setDescription("Updated Task Description");

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage("Task updated successfully");
        baseResponse.setStatus(HttpStatus.OK.value());

        when(projectService.updateTask(any(CreateTaskRequest.class), eq(1L)))
                .thenReturn(new ResponseEntity<>(baseResponse, HttpStatus.OK));

        mockMvc.perform(put("/api/v1/tasks/{taskId}", 1L)
                        .contentType("application/json")
                        .content("{\"title\":\"Updated Task Title\", \"description\":\"Updated Task Description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Task updated successfully"))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()));

        verify(projectService, times(1)).updateTask(any(CreateTaskRequest.class), eq(1L));
    }

    @Test
    public void testDeleteTask() throws Exception {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage("Task deleted successfully");
        baseResponse.setStatus(HttpStatus.OK.value());

        when(projectService.deleteTask(eq(1L))).thenReturn(new ResponseEntity<>(baseResponse, HttpStatus.OK));

        mockMvc.perform(delete("/api/v1/tasks/{taskId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Task deleted successfully"))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()));

        verify(projectService, times(1)).deleteTask(eq(1L));
    }


}
