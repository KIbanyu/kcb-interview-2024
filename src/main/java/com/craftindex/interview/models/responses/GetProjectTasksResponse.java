package com.craftindex.interview.models.responses;

import com.craftindex.interview.entities.TaskEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class GetProjectTasksResponse extends BaseResponse {
    private List<TaskEntity> tasks;
}
