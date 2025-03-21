package com.practice.hrbank.mapper;

import com.practice.hrbank.dto.employee.EmployeeDto;
import com.practice.hrbank.entity.Employee;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

  public EmployeeDto toDto(Employee employee) {
    return new EmployeeDto(
        employee.getId(),
        employee.getName(),
        employee.getEmail(),
        employee.getEmployeeNumber(),
        employee.getDepartment().getId(),
        employee.getDepartment().getName(),
        employee.getPosition(),
        employee.getHireDate(),
        employee.getStatus(),
        employee.getProfileImage() == null ? null : employee.getProfileImage().getId()
    );
  }

  public List<EmployeeDto> toDtoList(List<Employee> employees) {
    if (employees == null) {
      return List.of();
    }
    return employees.stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }
}
