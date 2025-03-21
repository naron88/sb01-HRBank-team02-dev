package com.practice.hrbank.controller;

import com.practice.hrbank.dto.backup.BackupDto;
import com.practice.hrbank.entity.Metadata;
import com.practice.hrbank.repository.MetadataRepository;
import com.practice.hrbank.service.BackupService;
import com.practice.hrbank.service.MetadataService;
import com.practice.hrbank.storage.EmployeesStorage;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/files")
public class FileManagementController {

  private final EmployeesStorage employeesStorage;
  private final MetadataService metadataService;

  @GetMapping("/{id}/download")
  public ResponseEntity<?> download(
    @PathVariable("id") Long metadataId) throws IOException {
    Metadata metadata = metadataService.findById(metadataId);
    return employeesStorage.download(metadata);
  }
}
