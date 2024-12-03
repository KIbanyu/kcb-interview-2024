package com.craftindex.interview.models.requests;

import com.craftindex.interview.enums.Status;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTaskRequest {
    private String title;
    private String description;
    private Status status;
    private LocalDate dueDate;
}
