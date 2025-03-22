package com.practice.hrbank.service;

import com.practice.hrbank.entity.Employee;
import com.practice.hrbank.entity.Metadata;
import com.practice.hrbank.repository.EmployeeRepository;
import com.practice.hrbank.repository.MetadataRepository;
import com.practice.hrbank.storage.BinaryContentStorage;
import com.practice.hrbank.storage.EmployeesStorage;
import com.practice.hrbank.storage.local.LocalLogFileStorage;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class MetadataService {

  private final MetadataRepository metadataRepository;
  private final EmployeeRepository employeeRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final EmployeesStorage employeesStorage;
  private final LocalLogFileStorage localLogFileStorage;

  public Metadata save(Metadata metadata) {
    return metadataRepository.save(metadata);
  }

  // 프로필 저장
  public Metadata createProfile(MultipartFile profile) throws IOException {
    Metadata metadata = new Metadata(profile.getName(), profile.getContentType(),
        profile.getSize());
    System.out.println("profile create service");
    metadataRepository.save(metadata);
    binaryContentStorage.createFile(metadata.getId(), profile.getBytes());
    return metadata;
  }

  // csv 백업 파일 생성
  public Metadata createEmployeesFile(Long backUpId) throws IOException {
    List<Employee> employees = employeeRepository.findAll();
    String name = "employee_backup_" + backUpId;
    Long size = employeesStorage.save(backUpId, employees);
    return new Metadata(name, "text/csv", size);
  }

  // 에러 로그 생성
  public Metadata createErrorLogFile(Instant time, String errorMessage) throws IOException {
    long size = localLogFileStorage.createFile(time, errorMessage);
    return new Metadata(time.toString(), "text/log", size);
  }

  public Metadata findById(Long id) {
    return metadataRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Metadata not found. id: " + id));
  }
}
