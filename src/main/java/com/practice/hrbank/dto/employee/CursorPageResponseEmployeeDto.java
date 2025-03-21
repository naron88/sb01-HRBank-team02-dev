package com.practice.hrbank.dto.employee;

import java.util.List;

public record CursorPageResponseEmployeeDto<EmployeeDto> (
    List<EmployeeDto> content,
    String nextCursor,
    Long nextIdAfter,
    int size,
    Long totalElements,
    boolean hasNext
) {

}