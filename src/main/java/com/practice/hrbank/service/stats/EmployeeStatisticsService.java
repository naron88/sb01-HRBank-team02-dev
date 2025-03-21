package com.practice.hrbank.service.stats;

import com.practice.hrbank.entity.Employee.Status;
import com.practice.hrbank.repository.EmployeeRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeStatisticsService {

  private final EmployeeRepository employeeRepository;

  // 지정된 조건에 맞는 직원 수를 조회
  public int countEmployees(String status, LocalDate fromDate, LocalDate toDate) {
    // 지정된 조건에 맞는 직원 수를 조회합니다. 상태 필터링 및 입사일 기간 필터링이 가능합니다.
    // 직원 상태 (재직중, 휴직중, 퇴사) Available values : ACTIVE, ON_LEAVE, RESIGNED
    // 입사일 시작 (지정 시 해당 기간 내 입사한 직원 수 조회, 미지정 시 전체 직원 수 조회)
    LocalDate startDate = (fromDate == null) ? LocalDate.of(1900, 1, 1) : fromDate;
    // 입사일 종료 (fromDate와 함께 사용, 기본값: 현재 일시)
    LocalDate endDate = (toDate == null) ? LocalDate.now() : toDate;
    if (status == null) {
      return employeeRepository.countByHireDateBetween(startDate, endDate);
    }
    if (!List.of("ACTIVE", "ON_LEAVE", "RESIGNED").contains(status.toUpperCase())) {
      throw new IllegalArgumentException("올바르지 않은 상태입니다: " + status);
    }
    Status enumStatus = Status.valueOf(status.toUpperCase());
    return employeeRepository.countByStatusAndHireDateBetween(enumStatus, startDate, endDate);
  }

  // 총 직원 수 조회
  public long countTotalEmployees() {
    return employeeRepository.count();
  }

  // 최근 1주일간 직원 정보 수정 건수 조회
  public int countRecentEmployeeUpdates() {
    Instant oneWeekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
    return employeeRepository.countByUpdatedAtGreaterThanEqual(oneWeekAgo);
  }

  // 이번 달 입사자 수 조회
  public int countNewHiresThisMonth() {
    LocalDate now = LocalDate.now();
    LocalDate firstDayOfMonth = now.withDayOfMonth(1);
    return employeeRepository.countByHireDateBetween(firstDayOfMonth, now);
  }
}
