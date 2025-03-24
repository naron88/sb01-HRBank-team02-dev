package com.practice.hrbank.dto.changeLog;

import java.time.Instant;

public record ChangeLogRequestDto(
    String employeeNumber,
    String type,
    String memo,
    String ipAddress,
    Instant atFrom,
    Instant atTo,
    Long idAfter,
    String cursor,
    Integer size,
    String sortField,
    String sortDirection
) {}
