package com.practice.hrbank.storage;

import com.practice.hrbank.entity.Employee;
import com.practice.hrbank.entity.Metadata;
import java.io.IOException;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface EmployeesStorage {

  Long save(Long backupId, List<Employee> employees) throws IOException;

  ResponseEntity<?> download(Metadata metadata) throws IOException;

}
