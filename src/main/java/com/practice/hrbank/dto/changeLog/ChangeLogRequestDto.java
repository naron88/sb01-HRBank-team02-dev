package com.practice.hrbank.dto.changeLog;

public record ChangeLogRequestDto(
        String employeeNumber,
        String type,
        String memo,
        String ipAddress,
        String atFrom,
        String atTo,
        Long idAfter,
        String cursor,
        Integer size,
        String sortField,
        String sortDirection
) {}
