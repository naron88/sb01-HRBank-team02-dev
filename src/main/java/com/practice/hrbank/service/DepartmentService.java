package com.practice.hrbank.service;

import com.practice.hrbank.dto.department.CursorPageResponseDepartmentDto;
import com.practice.hrbank.dto.department.DepartmentCreateRequest;
import com.practice.hrbank.dto.department.DepartmentDto;
import com.practice.hrbank.dto.department.DepartmentUpdateRequest;
import com.practice.hrbank.entity.Department;
import com.practice.hrbank.repository.DepartmentRepository;
import com.practice.hrbank.repository.EmployeeRepository;
import com.practice.hrbank.util.CursorPaginationUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Expression;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository departmentRepository,
        EmployeeRepository employeeRepository) {
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

    public CursorPageResponseDepartmentDto findAll(
        String nameOrDescription,
        Long idAfter,
        String cursor,
        int size,
        String sortField,
        String sortDirection
    ) {
        if (cursor != null) {
            try {
                idAfter = CursorPaginationUtils.decodeCursor(cursor);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid cursor format");
            }
        }

        Specification<Department> spec = Specification
            .where(hasKeyword(nameOrDescription))
            .and(afterCursor(sortField, sortDirection, cursor));

        Pageable pageable = CursorPaginationUtils.createPageable(
            size,
            sortField,
            sortDirection,
            "name", // 기본 정렬 필드
            "asc"               // 기본 정렬 방향
        );

        Page<Department> departments = departmentRepository.findAll(spec, pageable);

        List<DepartmentDto> dtoList = departments.getContent()
            .stream()
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
            .toList();

        Long nextIdAfter = null;
        String nextCursor = null;

        if (!dtoList.isEmpty()) {
            nextIdAfter = dtoList.get(dtoList.size() - 1).id();
            nextCursor = CursorPaginationUtils.encodeCursor(nextIdAfter);
        }

        return new CursorPageResponseDepartmentDto(
            dtoList,
            nextCursor,
            nextIdAfter,
            departments.getSize(),
            departments.getTotalElements(),
            departments.hasNext()
        );

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
        if (departmentUpdateRequest.name() != null && !departmentUpdateRequest.name()
            .equals(department.getName())) {
            departmentRepository.findByName(departmentUpdateRequest.name())
                .filter(existingDepartment -> !existingDepartment.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("이미 존재하는 부서명입니다.");
                });
        }

        // null이 아닌 값만 업데이트
        department.update(departmentUpdateRequest.name(), departmentUpdateRequest.description(),
            departmentUpdateRequest.establishedDate());
        department = departmentRepository.save(department);
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

    private Specification<Department> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }
            String pattern = "%" + keyword + "%";
            return cb.or(
                cb.like(root.get("name"), pattern),
                cb.like(root.get("description"), pattern)
            );
        };
    }

    private Specification<Department> afterCursor(String sortField, String direction,
        String cursorValue) {
        return (root, query, cb) -> {
            if (cursorValue == null || sortField == null) {
                return null;
            }

            Expression<?> path = root.get(sortField);

            if (path.getJavaType() == LocalDate.class) {
                LocalDate value = LocalDate.parse(cursorValue);
                return "desc".equalsIgnoreCase(direction)
                    ? cb.lessThan(path.as(LocalDate.class), value)
                    : cb.greaterThan(path.as(LocalDate.class), value);
            } else {
                return "desc".equalsIgnoreCase(direction)
                    ? cb.lessThan(path.as(String.class), cursorValue)
                    : cb.greaterThan(path.as(String.class), cursorValue);
            }
        };
    }

}
