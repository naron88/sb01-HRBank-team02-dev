package com.practice.hrbank.service.stats;

import com.practice.hrbank.dto.dashboard.EmployeeDistributionDto;
import com.practice.hrbank.entity.Employee;
import com.practice.hrbank.entity.Employee.Status;
import com.practice.hrbank.repository.EmployeeRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeDistributionService {

  private final EmployeeRepository employeeRepository;

  // 지정된 기준으로 그룹화된 직원 분포를 조회
  public List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy,
      String status) {
    if (!List.of("department", "position").contains(groupBy)) {
      throw new IllegalArgumentException("올바르지 않은 그룹입니다. : " + groupBy);
    }
    if (!List.of("ACTIVE", "ON_LEAVE", "RESIGNED").contains(status.toUpperCase())) {
      throw new IllegalArgumentException("올바르지 않은 상태입니다: " + status);
    }
    Status enumStatus = Status.valueOf(status);
    List<EmployeeDistributionDto> distributionDtoList = new ArrayList<>();
    List<Employee> employeeList = employeeRepository.findAllByStatus(enumStatus);
    int totalCount = employeeList.size();
    Map<String, Integer> groupedCount = new HashMap<>();
    if (groupBy.equals("department")) {
      employeeList
          .forEach(employee -> {
            String name = employee.getDepartment().getName();
            groupedCount.put(name, groupedCount.getOrDefault(name, 0) + 1);
          });
    } else {
      employeeList
          .forEach(employee -> {
        String name = employee.getPosition();
        groupedCount.put(name, groupedCount.getOrDefault(name, 0) + 1);
      });
    }
    for (String key : groupedCount.keySet()) {
      double percentage = (double) groupedCount.get(key) / totalCount * 100;
      double roundedPercentage = Math.round(percentage * 10.0) / 10.0;
      distributionDtoList.add(
          new EmployeeDistributionDto(key, groupedCount.get(key), roundedPercentage));
    }
    return distributionDtoList;
  }

  // 부서별 직원 분포 조회
  public List<EmployeeDistributionDto> getEmployeeDistributionByDepartment() {
    List<Employee> employeeList = employeeRepository.findAll();
    int totalCount = employeeList.size();
    Map<String, Integer> groupedCount = new HashMap<>();
    employeeList
        .forEach(employee -> {
          String name = employee.getDepartment().getName();
          groupedCount.put(name, groupedCount.getOrDefault(name, 0) + 1);
        });
    List<EmployeeDistributionDto> distributionDtoList = new ArrayList<>();
    for (String key : groupedCount.keySet()) {
      double percentage = (double) groupedCount.get(key) / totalCount * 100;
      double roundedPercentage = Math.round(percentage * 10.0) / 10.0;
      distributionDtoList.add(
          new EmployeeDistributionDto(key, groupedCount.get(key), roundedPercentage));
    }
    return distributionDtoList;
  }

  // 직무별 직원 분포 조회
  public List<EmployeeDistributionDto> getEmployeeDistributionByPosition() {
    List<Employee> employeeList = employeeRepository.findAll();
    int totalCount = employeeList.size();
    Map<String, Integer> groupedCount = new HashMap<>();
    employeeList.forEach(employee -> {
      String name = employee.getPosition();
      groupedCount.put(name, groupedCount.getOrDefault(name, 0) + 1);
    });
    List<EmployeeDistributionDto> distributionDtoList = new ArrayList<>();
    for (String key : groupedCount.keySet()) {
      double percentage = (double) groupedCount.get(key) / totalCount * 100;
      double roundedPercentage = Math.round(percentage * 10.0) / 10.0;
      distributionDtoList.add(
          new EmployeeDistributionDto(key, groupedCount.get(key), roundedPercentage));
    }
    return distributionDtoList;
  }
}
