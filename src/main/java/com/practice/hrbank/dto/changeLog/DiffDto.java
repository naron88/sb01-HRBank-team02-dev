package com.practice.hrbank.dto.changeLog;

public record DiffDto(
        String propertyName,
        String before,
        String after) {

}
