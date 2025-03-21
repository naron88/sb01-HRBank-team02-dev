//package com.practice.hrbank.service.stats;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.practice.hrbank.dto.dashboard.EmployeeDistributionDto;
//import com.practice.hrbank.entity.Department;
//import com.practice.hrbank.entity.Employee;
//import com.practice.hrbank.entity.Employee.Status;
//import com.practice.hrbank.entity.Metadata;
//import com.practice.hrbank.repository.EmployeeRepository;
//import java.time.LocalDate;
//import java.util.List;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.api.extension.Extension;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//class EmployeeDistributionServiceTest {
//
//  @InjectMocks
//  private EmployeeDistributionService employeeDistributionService;
//
//  @Mock
//  private EmployeeRepository employeeRepository;
//
//  @Test
//  void getEmployeeDistribution_Success_Department() {
//    // given
//    String groupBy = "department";
//    String status = "ACTIVE";
//    Status enumStatus = Status.valueOf(status);
//    List<Employee> employeeList = List.of(
//        // 테스트를 위해 생성자를 변경했습니다.
//        new Employee(1L, "test1", "test1@gmail.com", "1", "test1", LocalDate.now(), new Metadata("test1", "test/txt", 1L), new Department("deptTest1", "test", LocalDate.now()), Status.ACTIVE),
//        new Employee(2L, "test2", "test2@gmail.com", "2", "test2", LocalDate.now(), new Metadata("test1", "test/txt", 1L), new Department("deptTest2", "test", LocalDate.now()), Status.ACTIVE),
//        new Employee(3L, "test3", "test3@gmail.com", "3", "test2", LocalDate.now(), new Metadata("test1", "test/txt", 1L), new Department("deptTest2", "test", LocalDate.now()), Status.ACTIVE)
//    );
//
//    // when
//    when(employeeRepository.findAllByStatus(enumStatus)).thenReturn(employeeList);
//    List<EmployeeDistributionDto> result = employeeDistributionService.getEmployeeDistribution(groupBy, status);
//
//    // then
//    assertNotNull(result);
//    assertEquals(result.size(), 2);
//    assertEquals(result.get(0).groupKey(), "deptTest2");
//    assertEquals(result.get(0).count(), 2);
//    assertEquals(result.get(0).percentage(), 66.7);
//    assertEquals(result.get(1).groupKey(), "deptTest1");
//    assertEquals(result.get(1).count(), 1);
//    assertEquals(result.get(1).percentage(), 33.3);
//    verify(employeeRepository, times(1)).findAllByStatus(enumStatus);
//  }
//
//  @Test
//  void getEmployeeDistribution_Success_Position() {
//    // given
//    String groupBy = "position";
//    String status = "ACTIVE";
//    Status enumStatus = Status.valueOf(status);
//    List<Employee> employeeList = List.of(
//        // 테스트를 위해 생성자를 변경했습니다.
//        new Employee(1L, "test1", "test1@gmail.com", "1", "positionTest1", LocalDate.now(), new Metadata("test1", "test/txt", 1L), new Department("deptTest1", "test", LocalDate.now()), Status.ACTIVE),
//        new Employee(2L, "test2", "test2@gmail.com", "2", "positionTest2", LocalDate.now(), new Metadata("test1", "test/txt", 1L), new Department("deptTest2", "test", LocalDate.now()), Status.ACTIVE),
//        new Employee(3L, "test3", "test3@gmail.com", "3", "positionTest2", LocalDate.now(), new Metadata("test1", "test/txt", 1L), new Department("deptTest2", "test", LocalDate.now()), Status.ACTIVE)
//    );
//
//    // when
//    when(employeeRepository.findAllByStatus(enumStatus)).thenReturn(employeeList);
//    List<EmployeeDistributionDto> result = employeeDistributionService.getEmployeeDistribution(groupBy, status);
//
//    // then
//    assertNotNull(result);
//    assertEquals(result.size(), 2);
//    assertEquals(result.get(0).groupKey(), "positionTest2");
//    assertEquals(result.get(0).count(), 2);
//    assertEquals(result.get(0).percentage(), 66.7);
//    assertEquals(result.get(1).groupKey(), "positionTest1");
//    assertEquals(result.get(1).count(), 1);
//    assertEquals(result.get(1).percentage(), 33.3);
//    verify(employeeRepository, times(1)).findAllByStatus(enumStatus);
//  }
//
//  @Test
//  void getEmployeeDistribution_InvalidGroupBy() {
//    // given
//    String groupBy = "Invalid group";
//    String status = "ACTIVE";
//    Status enumStatus = Status.valueOf(status);
//
//    // when
//
//    // then
//    assertThrows(IllegalArgumentException.class, () -> employeeDistributionService.getEmployeeDistribution(groupBy, status));
//    verify(employeeRepository, never()).findAllByStatus(enumStatus);
//  }
//
//  @Test
//  void getEmployeeDistribution_InvalidStatus() {
//    // given
//    String groupBy = "position";
//    String status = "Invalid Status";
//
//    // when
//
//    // then
//    assertThrows(IllegalArgumentException.class, () -> employeeDistributionService.getEmployeeDistribution(groupBy, status));
//    verify(employeeRepository, never()).findAllByStatus(any(Status.class));
//  }
//
//  @Test
//  void getEmployeeDistributionByDepartment_Success() {
//    // given
//    List<Employee> employeeList = List.of(
//        // 테스트를 위해 생성자를 변경했습니다.
//        new Employee(1L, "test1", "test1@gmail.com", "1", "test1", LocalDate.now(), new Metadata("test1", "test/txt", 1L), new Department("deptTest1", "test", LocalDate.now()), Status.ACTIVE),
//        new Employee(2L, "test2", "test2@gmail.com", "2", "test2", LocalDate.now(), new Metadata("test1", "test/txt", 1L), new Department("deptTest2", "test", LocalDate.now()), Status.ACTIVE),
//        new Employee(3L, "test3", "test3@gmail.com", "3", "test2", LocalDate.now(), new Metadata("test1", "test/txt", 1L), new Department("deptTest2", "test", LocalDate.now()), Status.ACTIVE)
//    );
//
//    // when
//    when(employeeRepository.findAll()).thenReturn(employeeList);
//    List<EmployeeDistributionDto> result = employeeDistributionService.getEmployeeDistributionByDepartment();
//
//    // then
//    assertNotNull(result);
//    assertEquals(result.size(), 2);
//    assertEquals(result.get(0).groupKey(), "deptTest2");
//    assertEquals(result.get(0).count(), 2);
//    assertEquals(result.get(0).percentage(), 66.7);
//    assertEquals(result.get(1).groupKey(), "deptTest1");
//    assertEquals(result.get(1).count(), 1);
//    assertEquals(result.get(1).percentage(), 33.3);
//    verify(employeeRepository, times(1)).findAll();
//  }
//
//  @Test
//  void getEmployeeDistributionByPosition_Success() {
//    // given
//    List<Employee> employeeList = List.of(
//        // 테스트를 위해 생성자를 변경했습니다.
//        new Employee(1L, "test1", "test1@gmail.com", "1", "positionTest1", LocalDate.now(), new Metadata("test1", "test/txt", 1L), new Department("test1", "test", LocalDate.now()), Status.ACTIVE),
//        new Employee(2L, "test2", "test2@gmail.com", "2", "positionTest2", LocalDate.now(), new Metadata("test1", "test/txt", 1L), new Department("test2", "test", LocalDate.now()), Status.ACTIVE),
//        new Employee(3L, "test3", "test3@gmail.com", "3", "positionTest2", LocalDate.now(), new Metadata("test1", "test/txt", 1L), new Department("test2", "test", LocalDate.now()), Status.ACTIVE)
//    );
//
//    // when
//    when(employeeRepository.findAll()).thenReturn(employeeList);
//    List<EmployeeDistributionDto> result = employeeDistributionService.getEmployeeDistributionByPosition();
//
//    // then
//    assertNotNull(result);
//    assertEquals(result.size(), 2);
//    assertEquals(result.get(0).groupKey(), "positionTest2");
//    assertEquals(result.get(0).count(), 2);
//    assertEquals(result.get(0).percentage(), 66.7);
//    assertEquals(result.get(1).groupKey(), "positionTest1");
//    assertEquals(result.get(1).count(), 1);
//    assertEquals(result.get(1).percentage(), 33.3);
//    verify(employeeRepository, times(1)).findAll();
//  }
//
//}