package com.practice.hrbank.dto.changeLog;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Instant timestamp;
    private int status;
    private String message;
    private String details;

    public static ErrorResponseDto of(int status, String message, String details) {
        return new ErrorResponseDto(Instant.now(), status, message, details);
    }
}
