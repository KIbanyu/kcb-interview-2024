package com.craftindex.interview.models.requests;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTaskRequest {
    private String title;
    private String description;
    private String status;
    private LocalDate dueDate;
}
