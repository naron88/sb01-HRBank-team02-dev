package com.practice.hrbank.dto.employee;

import com.practice.hrbank.entity.Employee;
import java.time.LocalDate;

public record EmployeeDto(
    Long id,
    String name,
    String email,
    String employeeNumber,
    Long departmentId,
    String departmentName,
    String position,
    LocalDate hireDate,
    Employee.Status status,
    Long profileImageId
) {

}
