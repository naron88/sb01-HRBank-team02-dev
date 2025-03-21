package com.practice.hrbank.exception;

import com.practice.hrbank.dto.changeLog.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 Bad Request - 잘못된 요청 처리
    @ExceptionHandler({IllegalArgumentException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponseDto> handleBadRequest(Exception ex) {
        return ResponseEntity.badRequest().body(ErrorResponseDto.of(
                HttpStatus.BAD_REQUEST.value(),
                "잘못된 요청입니다.",
                ex.getMessage()
        ));
    }

    // 404 Not Found - 데이터가 존재하지 않을 때
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponseDto.of(
                HttpStatus.NOT_FOUND.value(),
                "데이터를 찾을 수 없습니다.",
                ex.getMessage()
        ));
    }

    // 500 Internal Server Error - 서버 내부 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleServerError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponseDto.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "서버 오류",
                ex.getMessage()
        ));
    }
}