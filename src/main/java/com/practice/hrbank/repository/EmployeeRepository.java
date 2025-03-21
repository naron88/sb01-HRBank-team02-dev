package com.practice.hrbank.repository;

import com.practice.hrbank.entity.Employee;
import com.practice.hrbank.entity.Employee.Status;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long>,
    JpaSpecificationExecutor<Employee> {

  Page<Employee> findAll(Specification<Employee> spec, Pageable pageable);

  @Query("SELECT e.employeeNumber FROM Employee e WHERE e.employeeNumber LIKE :yearPrefix ORDER BY e.employeeNumber DESC")
  String findLatestEmployeeNumberByYear(@Param("yearPrefix") int yearPrefix);

  // 특정 날짜 이전에 입사한 직원 수 조회
  int countByHireDateBefore(LocalDate date);

  // 특정 상태의 직원 목록 조회
  List<Employee> findAllByStatus(Status status);

  // 특정 상태와 특정 기간 입사한 직업의 수
  int countByStatusAndHireDateBetween(Status status, LocalDate startDate,
      LocalDate endDate);

  // 최근 일주일 내 수정된 직원 수 조회
  int countByUpdatedAtGreaterThanEqual(Instant updatedAt);

  // 특정 기간 입사한 직원 수 조회
  int countByHireDateBetween(LocalDate startDate, LocalDate endDate);

  // 최근에 수정된 직원 조회
  Optional<Employee> findByUpdatedAtGreaterThan(Instant lastBatchTime);

  // 부서에 소속된 직원이 있는지 확인
  boolean existsByDepartmentId(Long departmentId);

  // id로 직원 수 조회
  int countByDepartmentId(Long departmentId);

}
