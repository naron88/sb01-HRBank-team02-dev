package com.practice.hrbank.dto.department;


import java.time.LocalDate;


public record   DepartmentDto(
        Long id,
        String name,
        String description,
        LocalDate establishedDate,
        int employeeCount
) {
}
