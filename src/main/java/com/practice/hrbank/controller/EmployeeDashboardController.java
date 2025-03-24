package com.practice.hrbank.controller;

import com.practice.hrbank.dto.dashboard.EmployeeDistributionDto;
import com.practice.hrbank.dto.dashboard.EmployeeTrendDto;
import com.practice.hrbank.service.DashboardService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeDashboardController {

  private final DashboardService dashboardService;

  @GetMapping("/stats/trend")
  ResponseEntity<List<EmployeeTrendDto>> getEmployeeTrend(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
      @RequestParam(required = false, defaultValue = "month") String unit
  ) {
    if (to == null) {
      to = LocalDate.now(); // 기본값: 현재 날짜
    }
    List<EmployeeTrendDto> employeeTrendDtoList = dashboardService.getEmployeeTrend(from, to, unit);
    return ResponseEntity.ok(employeeTrendDtoList);
  }

  @GetMapping("/stats/distribution")
  ResponseEntity<List<EmployeeDistributionDto>> getEmployeeDistribution(
      @RequestParam(defaultValue = "department") String groupBy,
      @RequestParam(defaultValue = "ACTIVE") String status
  ) {
    List<EmployeeDistributionDto> employeeDistributionDtoList = dashboardService.getEmployeeDistribution(groupBy, status);
    return ResponseEntity.ok(employeeDistributionDtoList);
  }

  @GetMapping("/count")
  ResponseEntity<Integer> countEmployees(
      @RequestParam(required = false) String status,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
  ) {
    if (toDate == null) {
      toDate = LocalDate.now(); // 기본 값: 현재 날짜
    }
    int count = dashboardService.countEmployees(status, fromDate, toDate);
    return ResponseEntity.ok(count);
  }
}
