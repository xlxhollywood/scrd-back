package org.example.scrd.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExceptionResponse {
    private String error;
    private String message;
}
