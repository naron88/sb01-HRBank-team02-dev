package com.practice.hrbank.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record EmployeeCreateRequest(

    @NotNull
    @Size(min = 1, max = 20)
    String name,

    @Email
    String email,

    @NotNull
    Long departmentId,

    @NotNull
    @Size(min = 1)
    String position,

    @NotNull
    LocalDate hireDate,

    String memo
) {

}
