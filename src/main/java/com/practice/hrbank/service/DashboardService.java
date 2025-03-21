package com.practice.hrbank.service;

import com.practice.hrbank.dto.dashboard.EmployeeDistributionDto;
import com.practice.hrbank.dto.dashboard.EmployeeTrendDto;
import com.practice.hrbank.entity.Employee.Status;
import com.practice.hrbank.service.stats.EmployeeDistributionService;
import com.practice.hrbank.service.stats.EmployeeStatisticsService;
import com.practice.hrbank.service.stats.EmployeeTrendService;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

  private final EmployeeTrendService employeeTrendService;
  private final EmployeeStatisticsService employeeStatisticsService;
  private final EmployeeDistributionService employeeDistributionService;
  private final BackupService backupService;

  // 지정된 기간 및 시간 단위로 그룹화된 직원 수 추이를 조회
  public List<EmployeeTrendDto> getEmployeeTrend(LocalDate from, LocalDate to, String unit) {
    return employeeTrendService.getEmployeeTrend(from, to, unit);
  }

  // 지정된 기준으로 그룹화된 직원 분포를 조회
  public List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy,
      String status) {
    return employeeDistributionService.getEmployeeDistribution(groupBy, status);
  }

  // 지정된 조건에 맞는 직원 수를 조회
  public int countEmployees(String status, LocalDate fromDate, LocalDate toDate) {
    return employeeStatisticsService.countEmployees(status, fromDate, toDate);
  }

  // 총 직원 수 조회
  public long countTotalEmployees() {
    return employeeStatisticsService.countTotalEmployees();
  }

  // 최근 1주일간 직원 정보 수정 건수 조회
  public int countRecentEmployeeUpdates() {
    return employeeStatisticsService.countRecentEmployeeUpdates();
  }

  // 이번 달 입사자 수 조회
  public int countNewHiresThisMonth() {
    return employeeStatisticsService.countNewHiresThisMonth();
  }

  // 마지막 백업 시간 조회
  public Instant findLastBackupTime() {
    return backupService.findLatest(null).startedAt();
  }

  // 최근 1년간 월별 직원 수 변동 추이 조회
  public List<EmployeeTrendDto> getMonthlyEmployeeTrend() {
    return employeeTrendService.getMonthlyEmployeeTrend();
  }

  // 부서별 직원 분포 조회
  public List<EmployeeDistributionDto> getEmployeeDistributionByDepartment() {
    return employeeDistributionService.getEmployeeDistributionByDepartment();
  }

  // 직무별 직원 분포 조회
  public List<EmployeeDistributionDto> getEmployeeDistributionByPosition() {
    return employeeDistributionService.getEmployeeDistributionByPosition();
  }
}
