package com.practice.hrbank.service;

import com.practice.hrbank.dto.changeLog.ChangeLogCreateRequest;
import com.practice.hrbank.dto.employee.CursorPageResponseEmployeeDto;
import com.practice.hrbank.dto.employee.EmployeeCreateRequest;
import com.practice.hrbank.dto.employee.EmployeeDto;
import com.practice.hrbank.dto.employee.EmployeeUpdateRequest;
import com.practice.hrbank.entity.ChangeLog.Type;
import com.practice.hrbank.entity.Department;
import com.practice.hrbank.entity.Employee;
import com.practice.hrbank.entity.Metadata;
import com.practice.hrbank.mapper.EmployeeMapper;
import com.practice.hrbank.repository.DepartmentRepository;
import com.practice.hrbank.repository.EmployeeRepository;
import jakarta.persistence.criteria.Predicate;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final EmployeeMapper employeeMapper;

  private final MetadataService metadataService;
  private final ChangeLogService changeLogService;

  private final DepartmentRepository departmentRepository;

  @Transactional
  public EmployeeDto create(EmployeeCreateRequest request, MultipartFile file, String ipAddress)
      throws IOException {
    validateDuplicateEmail(request.email());

    Metadata profile = file != null ? metadataService.createProfile(file) : null;
    Department department = departmentRepository.findById(request.departmentId())
        .orElseThrow(() -> new NoSuchElementException(
            "Department with id " + request.departmentId() + " not found"));
    String employeeNumber = generateEmployeeNumber(request.hireDate());

    Employee employee = new Employee(
        request.name(),
        request.email(),
        employeeNumber,
        request.position(),
        request.hireDate(),
        profile,
        department
    );

    EmployeeDto employeeDto = employeeMapper.toDto(employeeRepository.save(employee));
    ChangeLogCreateRequest changeLogCreateRequest = new ChangeLogCreateRequest(
        null,
        employeeDto,
        ipAddress,
        request.memo(),
        Type.CREATED
    );
    changeLogService.save(changeLogCreateRequest);

    return employeeDto;
  }

  @Transactional(readOnly = true)
  public CursorPageResponseEmployeeDto<EmployeeDto> searchEmployee(String nameOrEmail,
      String employeeNumber, String departmentName,
      String position, LocalDate hireDateFrom, LocalDate hireDateTo, Employee.Status status,
      Long idAfter, String cursor, Integer size, String sortField, String sortDirection) {

    Sort.Direction direction = Sort.Direction.fromString(sortDirection);
    Pageable pageable = PageRequest.of(0, size, Sort.by(direction, sortField));
    Specification<Employee> specification = buildSpecification(
        nameOrEmail, employeeNumber, departmentName, position, hireDateFrom, hireDateTo, status,
        idAfter, cursor
    );

    Page<Employee> employeePage = employeeRepository.findAll(specification, pageable);

    List<EmployeeDto> content = employeeMapper.toDtoList(employeePage.getContent());
    EmployeeDto lastEmployee = content.isEmpty() ? null : content.get(content.size() - 1);

    String nextCursor = lastEmployee != null ? encodeCursor(lastEmployee.id()) : null;
    Long nextIdAfter = lastEmployee != null ? lastEmployee.id() : null;
    boolean hasNext = employeePage.hasNext();

    return new CursorPageResponseEmployeeDto<>(
        content,
        nextCursor,
        nextIdAfter,
        size,
        employeePage.getTotalElements(),
        hasNext
    );
  }

  @Transactional(readOnly = true)
  public EmployeeDto findById(Long id) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Employee with id " + id + " not found"));

    return employeeMapper.toDto(employee);
  }

  @Transactional
  public EmployeeDto update(Long id, EmployeeUpdateRequest request, MultipartFile file,
      String ipAddress) throws IOException {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Employee with id " + id + " not found"));

    EmployeeDto beforeEmployeeDto = employeeMapper.toDto(employee);

    if (request.name() != null && !request.name().isBlank()) {
      employee.updateName(request.name());
    }
    if ( request.email() != null && !request.email().isBlank()){
      validateDuplicateEmail(request.email());
      employee.updateEmail(request.email());
    }
    if (request.position() != null && !request.position().isBlank()) {
      employee.updatePosition(request.position());
    }
    if (request.departmentId() != null) {
      Department department = departmentRepository.findById(request.departmentId())
          .orElseThrow(() -> new NoSuchElementException(
              "Department with id " + request.departmentId() + " not found"));
      employee.updateDepartment(department);
    }
    if (file != null) {
      Metadata profile = metadataService.createProfile(file);
      employee.updateProfile(profile);
    }
    if (request.status() != null) {
      employee.updateStatus(request.status());
    }

    EmployeeDto afterEmployeeDto = employeeMapper.toDto(employee);
    ChangeLogCreateRequest changeLogCreateRequest = new ChangeLogCreateRequest(
        beforeEmployeeDto,
        afterEmployeeDto,
        ipAddress,
        request.memo(),
        Type.UPDATED
    );
    changeLogService.save(changeLogCreateRequest);

    return afterEmployeeDto;
  }

  @Transactional
  public void delete(Long id, String ipAddress) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Employee with id " + id + " not found"));

    EmployeeDto employeeDto = employeeMapper.toDto(employee);
    ChangeLogCreateRequest changeLogCreateRequest = new ChangeLogCreateRequest(
        employeeDto,
        null,
        ipAddress,
        "직원 삭제",
        Type.DELETED
    );
    changeLogService.save(changeLogCreateRequest);

    employeeRepository.deleteById(id);
  }

  public String generateEmployeeNumber(LocalDate hireDate) {
    int hireYear = hireDate.getYear();
    String yearPrefix = String.format("EMP-%d-", hireYear);

    Optional<String> lastEmployeeNumber = employeeRepository.findLatestEmployeeNumberByYear(yearPrefix + "%");

    if (lastEmployeeNumber.isEmpty()) {
      return String.format("%s%03d", yearPrefix, 1);
    }

    int lastNumber = Integer.parseInt(lastEmployeeNumber.get().substring(lastEmployeeNumber.get().lastIndexOf("-") + 1));
    return String.format("%s%03d", yearPrefix, lastNumber + 1);
  }

  private String encodeCursor(Long id) {
    return Base64.getEncoder().encodeToString(id.toString().getBytes());
  }

  public void validateDuplicateEmail(String email) {
    employeeRepository.findAll()
        .forEach(employee -> employee.validateDuplicateEmail(email));
  }

  public Specification<Employee> buildSpecification(
      String nameOrEmail,
      String employeeNumber,
      String departmentName,
      String position,
      LocalDate hireDateFrom,
      LocalDate hireDateTo,
      Employee.Status status,
      Long idAfter,
      String cursor
  ) {
    return (root, query, criteriaBuilder) -> {
      Predicate predicate = criteriaBuilder.conjunction();

      if (nameOrEmail != null) {
        boolean isEmail = nameOrEmail.contains("@") && nameOrEmail.contains(".");
        if (isEmail) {
          predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + nameOrEmail.toLowerCase() + "%"));
        } else {
          predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + nameOrEmail.toLowerCase() + "%"));
        }
      }

      if (employeeNumber != null) {
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("employeeNumber")), "%" + employeeNumber.toLowerCase() + "%"));
      }

      if (departmentName != null) {
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("department").get("name")), "%" + departmentName.toLowerCase() + "%"));
      }

      if (position != null) {
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("position")), "%" + position.toLowerCase() + "%"));
      }

      if (hireDateFrom != null || hireDateTo != null) {
        if (hireDateFrom != null && hireDateTo != null) {
          predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get("hireDate"), hireDateFrom, hireDateTo));
        } else if (hireDateFrom != null) {
          predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("hireDate"), hireDateFrom));
        } else if (hireDateTo != null) {
          predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("hireDate"), hireDateTo));
        }
      }

      if (status != null) {
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), status));
      }

      if (idAfter != null) {
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThan(root.get("id"), idAfter));
      }

      if (cursor != null) {
        Long cursorId = decodeCursor(cursor);
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThan(root.get("id"), cursorId));
      }

      return predicate;
    };
  }

  private Long decodeCursor(String cursor) {
    byte[] decodedBytes = Base64.getDecoder().decode(cursor);
    return Long.parseLong(new String(decodedBytes));
  }
}
