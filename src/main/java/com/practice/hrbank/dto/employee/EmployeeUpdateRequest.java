package com.practice.hrbank.dto.employee;

import com.practice.hrbank.entity.Employee;
import java.time.LocalDate;

public record EmployeeUpdateRequest(
    String name,
    String email,
    Long departmentId,
    String position,
    LocalDate hireDate,
    Employee.Status status,
    String memo
) {

}
