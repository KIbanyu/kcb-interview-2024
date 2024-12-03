package com.craftindex.interview.models.requests;

import lombok.Data;

@Data
public class CreateProjectRequest {
    private String name;
    private String description;
}
