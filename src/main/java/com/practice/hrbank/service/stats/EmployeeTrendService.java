package com.practice.hrbank.service.stats;

import com.practice.hrbank.dto.dashboard.EmployeeTrendDto;
import com.practice.hrbank.repository.EmployeeRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeTrendService {

  private final EmployeeRepository employeeRepository;

  // 지정된 기간 및 시간 단위로 그룹화된 직원 수 추이를 조회
  public List<EmployeeTrendDto> getEmployeeTrend(LocalDate from, LocalDate to, String unit) {
    if (!List.of("day", "week", "quarter", "year", "month").contains(unit)) {
      throw new IllegalArgumentException("올바르지 않은 unit입니다. : " + unit);
    }
    // from: 시작 일시, 기본값: 현재로부터 unit 기준 12개 이전
    from = (from == null) ? getDefaultFrom(unit) : from;
    // to: 종료 일시, 기본값: 현재
    to = (to == null) ? LocalDate.now() : to;
    List<EmployeeTrendDto> trendList = new ArrayList<>();
    Integer previousCount = null;
    for (LocalDate date = from; date.isBefore(to) || date.isEqual(to);
        date = plusDateByUnit(date, unit)) {
      int count = employeeRepository.countByHireDateBefore(date.plusDays(1));
      int change = (previousCount == null)
          ? 0
          : count - previousCount;
      double changeRate = (previousCount == null || previousCount == 0)
          ? 0
          : (double) change * 100 / previousCount;
      double roundedChangeRate = Math.round(changeRate * 10.0) / 10.0;
      trendList.add(new EmployeeTrendDto(date, count, change, roundedChangeRate));
      previousCount = count;
    }
    return trendList;
  }

  // 최근 1년간 월별 직원 수 변동 추이 조회
  public List<EmployeeTrendDto> getMonthlyEmployeeTrend() {
    LocalDate now = LocalDate.now();
    LocalDate start = now.minusYears(1).withDayOfMonth(1);

    List<EmployeeTrendDto> trendList = new ArrayList<>();
    Integer previousCount = null;

    // 날짜 범위가 12개월이 되도록 수정 (now 전에까지)
    for (LocalDate date = start; date.isBefore(now); date = date.plusMonths(1)) {
      LocalDate endOfMonth = date.plusMonths(1).withDayOfMonth(1);
      int count = employeeRepository.countByHireDateBefore(endOfMonth);
      int change = (previousCount == null) ? 0 : count - previousCount;
      double changeRate = (previousCount == null || previousCount == 0) ? 0 : (double) change * 100 / previousCount;
      double roundedChangeRate = Math.round(changeRate * 10.0) / 10.0;
      trendList.add(new EmployeeTrendDto(date, count, change, roundedChangeRate));
      previousCount = count;
    }
    return trendList;
  }

  private LocalDate plusDateByUnit(LocalDate from, String unit) {
    return switch (unit.toLowerCase()) {
      case "day" -> from.plusDays(1);
      case "week" -> from.plusWeeks(1);
      case "quarter" -> from.plusMonths(3);
      case "year" -> from.plusYears(1);
      default -> from.plusMonths(1);
    };
  }

  private LocalDate getDefaultFrom(String unit) {
    LocalDate now = LocalDate.now();
    return switch (unit.toLowerCase()) {
      case "day" -> now.minusDays(12);
      case "week" -> now.minusWeeks(12);
      case "quarter" -> now.minusMonths(36);
      case "year" -> now.minusYears(12);
      default -> now.minusMonths(12);
    };
  }
}
