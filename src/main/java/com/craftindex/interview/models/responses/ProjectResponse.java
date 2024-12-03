package com.craftindex.interview.models.responses;

import com.craftindex.interview.entities.ProjectEntity;
import lombok.Builder;
import lombok.Data;

@Data
public class ProjectResponse extends BaseResponse {
    private ProjectEntity project;
}
