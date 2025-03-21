package com.practice.hrbank.controller;

import com.practice.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.practice.hrbank.dto.employee.EmployeeCreateRequest;
import com.practice.hrbank.dto.employee.EmployeeDto;
import com.practice.hrbank.dto.employee.EmployeeUpdateRequest;
import com.practice.hrbank.entity.Employee;
import com.practice.hrbank.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {

  private final EmployeeService employeeService;

  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<EmployeeDto> create(@Valid @RequestPart("request") EmployeeCreateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile,
      HttpServletRequest httpServletRequest) throws IOException {
    String ipAddress = httpServletRequest.getRemoteAddr();
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(employeeService.create(request, profile, ipAddress));
  }

  @GetMapping
  public ResponseEntity<CursorPageResponseEmployeeDto<EmployeeDto>> getEmployees(
      @RequestParam(value = "nameOrEmail", required = false) String nameOrEmail,
      @RequestParam(value = "employeeNumber", required = false) String employeeNumber,
      @RequestParam(value = "departmentName", required = false) String departmentName,
      @RequestParam(value = "position", required = false) String position,
      @RequestParam(value = "hireDateFrom", required = false) LocalDate hireDateFrom,
      @RequestParam(value = "hireDateTo", required = false) LocalDate hireDateTo,
      @RequestParam(value = "status", required = false) Employee.Status status,
      @RequestParam(value = "idAfter", required = false) Long idAfter,
      @RequestParam(value = "cursor", required = false) String cursor,
      @RequestParam(value = "size", defaultValue = "10") Integer size,
      @RequestParam(value = "sortField", defaultValue = "name") String sortField,
      @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection
  ) {
    CursorPageResponseEmployeeDto<EmployeeDto> response = employeeService.searchEmployee(
        nameOrEmail, employeeNumber, departmentName, position,
        hireDateFrom, hireDateTo, status, idAfter, cursor,
        size, sortField, sortDirection
    );
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<EmployeeDto> find(@PathVariable("id") Long id) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(employeeService.findById(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") Long id, HttpServletRequest httpServletRequest) {
    String ipAddress = httpServletRequest.getRemoteAddr();
    employeeService.delete(id, ipAddress);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @PatchMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<EmployeeDto> update(@PathVariable("id") Long id,
      @RequestPart("request") EmployeeUpdateRequest request,
      @RequestPart(value = "profile", required = false) MultipartFile profile,
      HttpServletRequest httpServletRequest) throws IOException {
    String ipAddress = httpServletRequest.getRemoteAddr();

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(employeeService.update(id, request, profile, ipAddress));
  }

}
