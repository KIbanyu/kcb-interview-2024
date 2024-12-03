package com.craftindex.interview.models.responses;

import com.craftindex.interview.entities.ProjectEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ProjectSummaryResponse {
    private ProjectEntity project;
    private int taskCounts;
}
