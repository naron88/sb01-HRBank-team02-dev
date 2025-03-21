package com.practice.hrbank.service;

import com.practice.hrbank.dto.department.DepartmentCreateRequest;
import com.practice.hrbank.dto.department.DepartmentDto;
import com.practice.hrbank.dto.department.DepartmentUpdateRequest;
import com.practice.hrbank.entity.Department;
import com.practice.hrbank.repository.DepartmentRepository;
import com.practice.hrbank.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public DepartmentDto findById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("부서를 찾을 수 없습니다."));

        // 해당 부서의 직원 수를 동적으로 계산
        int employeeCount = employeeRepository.countByDepartmentId(id);

        return new DepartmentDto(
                department.getId(),
                department.getName(),
                department.getDescription(),
                department.getEstablishedDate(),
                employeeCount
        );
    }

    public List<DepartmentDto> findAll(String nameOrDescription, String sortBy, Long lastId, int pageSize) {
        Pageable pageable = PageRequest.of(
                0, // 페이지 번호 (0부터 시작)
                pageSize, // 한 페이지 크기
                Sort.by(Sort.Order.asc(sortBy)) // 정렬 기준
        );

        Page<Department> departmentPage = departmentRepository.findByNameContainingOrDescriptionContaining(
                nameOrDescription, nameOrDescription, pageable
        );

        return departmentPage.stream()
                .map(department -> {
                    int employeeCount = employeeRepository.countByDepartmentId(department.getId());
                    return new DepartmentDto(
                            department.getId(),
                            department.getName(),
                            department.getDescription(),
                            department.getEstablishedDate(),
                            employeeCount
                    );
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public DepartmentDto create(DepartmentCreateRequest departmentCreateRequest) {
        if (departmentRepository.existsByName(departmentCreateRequest.name())) {
            throw new IllegalArgumentException("부서 중복");
        }

        Department department = new Department(
                departmentCreateRequest.name(),
                departmentCreateRequest.description(),
                departmentCreateRequest.establishedDate()
        );

        Department savedDepartment = departmentRepository.save(department);

        int employeeCount = employeeRepository.countByDepartmentId(savedDepartment.getId());

        return new DepartmentDto(
                savedDepartment.getId(),
                savedDepartment.getName(),
                savedDepartment.getDescription(),
                savedDepartment.getEstablishedDate(),
                employeeCount
        );
    }


    public DepartmentDto update(Long id, DepartmentUpdateRequest departmentUpdateRequest) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("부서를 찾을 수 없습니다."));

        // 이름이 변경되는 경우에만 중복 검사 수행
        if (departmentUpdateRequest.name() != null && !departmentUpdateRequest.name().equals(department.getName())) {
            departmentRepository.findByName(departmentUpdateRequest.name())
                    .filter(existingDepartment -> !existingDepartment.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException("이미 존재하는 부서명입니다.");
                    });
        }

        // null이 아닌 값만 업데이트
        department.update(departmentUpdateRequest.name(), departmentUpdateRequest.description(), departmentUpdateRequest.establishedDate());

        int employeeCount = employeeRepository.countByDepartmentId(department.getId());

        return new DepartmentDto(
                department.getId(),
                department.getName(),
                department.getDescription(),
                department.getEstablishedDate(),
                employeeCount

        );
    }

    @Transactional
    public boolean delete(Long departmentId) {
        if (employeeRepository.existsByDepartmentId(departmentId)) {
            return false;
        }

        return departmentRepository.findById(departmentId)
                .map(department -> {
                    departmentRepository.delete(department);
                    return true;
                })
                .orElse(false);
    }
}
