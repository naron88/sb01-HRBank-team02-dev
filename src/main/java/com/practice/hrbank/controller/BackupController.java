package com.practice.hrbank.controller;

import com.practice.hrbank.dto.backup.BackupDto;
import com.practice.hrbank.dto.backup.CursorPageResponseBackupDto;
import com.practice.hrbank.service.BackupService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backups")
@RequiredArgsConstructor
public class BackupController {

  private final BackupService backupService;

  @GetMapping
  public ResponseEntity<CursorPageResponseBackupDto> findAll(
      @RequestParam(required = false) String worker,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) Instant startedAtFrom,
      @RequestParam(required = false) Instant startedAtTo,
      @RequestParam(required = false) Long idAfter,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) Integer size,
      @RequestParam(required = false) String sortField,
      @RequestParam(required = false) String sortDirection
  ) {
    CursorPageResponseBackupDto response = backupService.findAll(worker, status, startedAtFrom,
        startedAtTo, idAfter, cursor, size, sortField, sortDirection);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<BackupDto> create(HttpServletRequest request) throws IOException {
    String clientIp = request.getHeader("X-Forwarded-For");
    if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
      clientIp = request.getRemoteAddr();
    }
    BackupDto backup = backupService.create(clientIp);
    return ResponseEntity.ok(backup);
  }

  @GetMapping("/latest")
  public ResponseEntity<BackupDto> getLatest(
      @RequestParam(required = false) String status
  ) {
    BackupDto backup = backupService.findLatest(status);

    return ResponseEntity.ok(backup);
  }

}
