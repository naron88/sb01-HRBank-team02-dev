package com.practice.hrbank.dto.dashboard;

public record EmployeeDistributionDto(
    String groupKey,
    int count,
    double percentage
) {

}
