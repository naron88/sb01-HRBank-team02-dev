//package com.practice.hrbank.service.stats;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyNoMoreInteractions;
//import static org.mockito.Mockito.when;
//
//import com.practice.hrbank.dto.dashboard.EmployeeTrendDto;
//import com.practice.hrbank.repository.EmployeeRepository;
//import java.time.LocalDate;
//import java.util.List;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//class EmployeeTrendServiceTest {
//
//  @InjectMocks
//  private EmployeeTrendService employeeTrendService;
//
//  @Mock
//  private EmployeeRepository employeeRepository;
//
//  @Test
//  void getEmployeeTrend_Success() {
//    // given
//    LocalDate from = LocalDate.of(2025, 3, 1);
//    LocalDate to = LocalDate.of(2025, 3, 6);
//    String unit = "day";
//
//    // when
//    when(employeeRepository.countByHireDateBefore(LocalDate.of(2025, 3, 2))).thenReturn(1);
//    when(employeeRepository.countByHireDateBefore(LocalDate.of(2025, 3, 3))).thenReturn(3);
//    when(employeeRepository.countByHireDateBefore(LocalDate.of(2025, 3, 4))).thenReturn(5);
//    when(employeeRepository.countByHireDateBefore(LocalDate.of(2025, 3, 5))).thenReturn(9);
//    when(employeeRepository.countByHireDateBefore(LocalDate.of(2025, 3, 6))).thenReturn(13);
//    when(employeeRepository.countByHireDateBefore(LocalDate.of(2025, 3, 7))).thenReturn(20);
//    List<EmployeeTrendDto> result = employeeTrendService.getEmployeeTrend(from, to, unit);
//
//    // then
//    assertEquals(from, result.get(0).date());
//    assertEquals(from.plusDays(1), result.get(1).date());
//    assertEquals(5, result.get(2).count());
//    assertEquals(9, result.get(3).count());
//    assertEquals(4, result.get(4).change());
//    assertEquals(44.4, result.get(4).changeRate());
//    verify(employeeRepository, times(6)).countByHireDateBefore(any(LocalDate.class));
//  }
//
//  @Test
//  void getEmployeeTrend_InvalidUnit() {
//    // given
//    LocalDate from = LocalDate.of(2025, 3, 1);
//    LocalDate to = LocalDate.of(2025, 3, 6);
//    String unit = "qwe";
//
//    // when
//
//    // then
//    assertThrows(IllegalArgumentException.class, () -> employeeTrendService.getEmployeeTrend(from, to, unit));
//    verify(employeeRepository, never()).countByHireDateBefore(any(LocalDate.class));
//  }
//
//  @Test
//  void etMonthlyEmployeeTrend_Success() {
//    // given
//    LocalDate now = LocalDate.now();
//    LocalDate start = now.minusYears(1).withDayOfMonth(1);
//
//    // when
//    when(employeeRepository.countByHireDateBefore(start.plusMonths(1))).thenReturn(1);
//    when(employeeRepository.countByHireDateBefore(start.plusMonths(2))).thenReturn(3);
//    when(employeeRepository.countByHireDateBefore(start.plusMonths(3))).thenReturn(5);
//    when(employeeRepository.countByHireDateBefore(start.plusMonths(4))).thenReturn(9);
//    when(employeeRepository.countByHireDateBefore(start.plusMonths(5))).thenReturn(13);
//    when(employeeRepository.countByHireDateBefore(start.plusMonths(6))).thenReturn(20);
//    List<EmployeeTrendDto> result = employeeTrendService.getMonthlyEmployeeTrend();
//
//    // then
//    assertEquals(start, result.get(0).date());
//    assertEquals(start.plusMonths(1), result.get(1).date());
//    assertEquals(5, result.get(2).count());
//    assertEquals(9, result.get(3).count());
//    assertEquals(4, result.get(4).change());
//    assertEquals(44.4, result.get(4).changeRate());
//    verify(employeeRepository, times(13)).countByHireDateBefore(any(LocalDate.class));
//  }
//}