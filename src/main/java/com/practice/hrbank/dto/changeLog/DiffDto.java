package com.practice.hrbank.dto.changeLog;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;

public record DiffDto(
        String propertyName,
        String before,
        String after) {

}
