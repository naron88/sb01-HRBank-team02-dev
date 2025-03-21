package com.practice.hrbank.dto.dashboard;

import java.time.LocalDate;

public record EmployeeTrendDto(
    LocalDate date,
    int count,
    int change,
    double changeRate
) {

}
