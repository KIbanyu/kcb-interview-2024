package com.craftindex.interview.models.responses;

import com.craftindex.interview.entities.ProjectEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class GetProjectsResponse extends BaseResponse {
    private List<ProjectEntity> projects;
}
