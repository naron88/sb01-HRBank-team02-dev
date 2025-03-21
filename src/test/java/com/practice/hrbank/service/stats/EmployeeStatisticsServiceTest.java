//package com.practice.hrbank.service.stats;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.practice.hrbank.entity.Employee.Status;
//import com.practice.hrbank.repository.EmployeeRepository;
//import java.time.Instant;
//import java.time.LocalDate;
//import java.time.temporal.ChronoUnit;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//class EmployeeStatisticsServiceTest {
//
//  @InjectMocks
//  private EmployeeStatisticsService employeeStatisticsService;
//
//  @Mock
//  private EmployeeRepository employeeRepository;
//
//  @Test
//  void countEmployees_Success_StatusIsNull() {
//    // given
//    LocalDate from = LocalDate.MIN;
//    LocalDate to = LocalDate.now();
//    int count = 3;
//
//    // when
//    when(employeeRepository.countByHireDateBetween(from, to)).thenReturn(count);
//    int result = employeeStatisticsService.countEmployees(null, null, null);
//
//    // then
//    assertEquals(result, count);
//    verify(employeeRepository, times(1)).countByHireDateBetween(from, to);
//    verify(employeeRepository, never()).countByStatusAndHireDateBetween(any(Status.class),any(LocalDate.class), any(
//        LocalDate.class));
//  }
//
//  @Test
//  void countEmployees_Success_StatusIsResigned() {
//    // given
//    LocalDate from = LocalDate.MIN;
//    LocalDate to = LocalDate.now();
//    int count = 3;
//    String status1 = "RESIGNED";
//    Status status = Status.valueOf(status1);
//
//    // when
//    when(employeeRepository.countByStatusAndHireDateBetween(status, from, to)).thenReturn(count);
//    int result = employeeStatisticsService.countEmployees(status1, from, to);
//
//    // then
//    assertEquals(result, count);
//    verify(employeeRepository, times(1)).countByStatusAndHireDateBetween(status, from, to);
//    verify(employeeRepository, never()).countByHireDateBetween(any(LocalDate.class), any(LocalDate.class));
//  }
//
//  @Test
//  void countEmployees_InvalidStatus() {
//    // given
//    LocalDate from = LocalDate.MIN;
//    LocalDate to = LocalDate.now();
//    String status1 = "QWER";
//
//    // when
//
//    // then
//    assertThrows(IllegalArgumentException.class, () -> employeeStatisticsService.countEmployees(status1, from, to));
//    verify(employeeRepository, never()).countByStatusAndHireDateBetween(any(Status.class),any(LocalDate.class), any(
//        LocalDate.class));
//    verify(employeeRepository, never()).countByHireDateBetween(any(LocalDate.class), any(LocalDate.class));
//  }
//
//  @Test
//  void countTotalEmployees_Success() {
//    // given
//    long count = 10L;
//
//    // when
//    when(employeeRepository.count()).thenReturn(count);
//    Long result = employeeStatisticsService.countTotalEmployees();
//
//    // then
//    assertEquals(result, count);
//    verify(employeeRepository, times(1)).count();
//  }
//
//  @Test
//  void countRecentEmployeeUpdates_Success() {
//    // given
//    int count = 10;
//
//    // when
//    when(employeeRepository.countByUpdatedAtGreaterThanEqual(any(Instant.class))).thenReturn(count);
//    int result = employeeStatisticsService.countRecentEmployeeUpdates();
//
//    // then
//    assertEquals(result, count);
//    verify(employeeRepository, times(1)).countByUpdatedAtGreaterThanEqual(any(Instant.class));
//  }
//
//  @Test
//  void countNewHiresThisMonth() {
//    // given
//    LocalDate now = LocalDate.now();
//    LocalDate firstDayOfMonth = now.withDayOfMonth(1);
//    int count = 10;
//
//    // when
//    when(employeeRepository.countByHireDateBetween(firstDayOfMonth, now)).thenReturn(count);
//    int result = employeeStatisticsService.countNewHiresThisMonth();
//
//    // then
//    assertEquals(result, count);
//    verify(employeeRepository, times(1)).countByHireDateBetween(firstDayOfMonth, now);
//  }
//}