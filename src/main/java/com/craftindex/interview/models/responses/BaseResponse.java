package com.craftindex.interview.models.responses;

import lombok.Builder;
import lombok.Data;

@Data
public class BaseResponse {
    private int status;
    private String message;
}
