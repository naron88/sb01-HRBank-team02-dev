package com.practice.hrbank.storage.local;

import static java.nio.file.Paths.get;

import com.practice.hrbank.entity.Employee;
import com.practice.hrbank.entity.Metadata;
import com.practice.hrbank.storage.EmployeesStorage;
import jakarta.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "hrbank.storage.type", havingValue = "local")
public class LocalEmployeesStorage implements EmployeesStorage {

  private final Path root;

  @Autowired
  public LocalEmployeesStorage(@Value("${hrbank.storage.local.paths.employees-path}") String root) {
    this.root = get(root);
  }

  @PostConstruct
  public void init() throws IOException {
    Files.createDirectories(root);
  }

  @Override
  public Long save(Long backupId, List<Employee> employees) throws IOException {
    String backupFileName = "employee_backup_" + backupId + ".csv";
    Path filePath = root.resolve(backupFileName);
    saveEmployeesToCsv(filePath, employees);
    return Files.size(filePath);
  }

  @Override
  public ResponseEntity<Resource> download(Metadata metadata) throws IOException {
    String backupFileName = metadata.getName() + ".csv";
    Path filePath = root.resolve(backupFileName);

    // 파일이 존재하는지 확인
    if (!Files.exists(filePath)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(null);
    }

    InputStream inputStream = Files.newInputStream(filePath);
    Resource resource = new InputStreamResource(inputStream);

    return ResponseEntity
        .status(HttpStatus.OK)
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + metadata.getName() + "\"")
        .header(HttpHeaders.CONTENT_TYPE, metadata.getContentType())
        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(metadata.getSize()))
        .body(resource);
  }

  private void saveEmployeesToCsv(Path filePath, List<Employee> employees) throws IOException {
    try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING)) {
      writer.write("ID,직원번호,이름,이메일,부서,직급,입사일,상태");
      writer.newLine();

      for (Employee emp : employees) {
        writer.write(String.join(",",
                emp.getId().toString(),
                emp.getEmployeeNumber(),
                emp.getName(),
                emp.getEmail(),
                emp.getDepartment().getName(),
                emp.getPosition(),
                emp.getHireDate().toString(),
                emp.getStatus().name()));
        writer.newLine();
      }
      writer.flush();
    }
  }
}
