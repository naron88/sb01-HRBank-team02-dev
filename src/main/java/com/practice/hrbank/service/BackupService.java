package com.practice.hrbank.service;

import static com.practice.hrbank.util.CursorPaginationUtils.createPageable;
import static com.practice.hrbank.util.CursorPaginationUtils.decodeCursor;
import static com.practice.hrbank.util.CursorPaginationUtils.encodeCursor;

import com.practice.hrbank.dto.backup.BackupDto;
import com.practice.hrbank.dto.backup.CursorPageResponseBackupDto;
import com.practice.hrbank.entity.Backup;
import com.practice.hrbank.entity.Backup.Status;
import com.practice.hrbank.entity.Metadata;
import com.practice.hrbank.mapper.BackupMapper;
import com.practice.hrbank.repository.BackupRepository;
import com.practice.hrbank.repository.EmployeeRepository;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BackupService {

  private final BackupRepository backupRepository;
  private final BackupMapper backupMapper;
  private final EmployeeRepository employeeRepository;
  private final MetadataService metadataService;

  public BackupDto create(String clientIp) throws IOException {
    if (isChanged()) {
      Backup backup = new Backup(
          null,
          Status.IN_PROGRESS,
          Instant.now(),
          null,
          clientIp
      );
      backup = backupRepository.save(backup);

      try {
        Metadata metadata = metadataService.createEmployeesFile(backup.getId());
        metadata = metadataService.save(metadata); // Metadata 먼저 저장
        backup.setFile(metadata);
        backup.setEndedAt(Instant.now());
        backup.setStatus(Status.COMPLETED);
        backup = backupRepository.save(backup);
        return backupMapper.toDto(backup);
      } catch (IOException e) {
        Metadata metadata = metadataService.createErrorLogFile(Instant.now(), e.getMessage());
        metadata = metadataService.save(metadata); // Metadata 먼저 저장
        backup.setFile(metadata);
        backup.setEndedAt(Instant.now());
        backup.setStatus(Status.FAILED);
        backup = backupRepository.save(backup);
        return backupMapper.toDto(backup);
      }
    } else {
      Backup skippedBackup = new Backup(
          null,
          Status.SKIPPED,
          Instant.now(),
          Instant.now(),
          clientIp
      );
      skippedBackup = backupRepository.save(skippedBackup);
      return backupMapper.toDto(skippedBackup);
    }
  }

  public BackupDto findById(Long id) {
    Backup backup = backupRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Backup not found. id: " + id));
    return backupMapper.toDto(backup);
  }

  public CursorPageResponseBackupDto findAll(String worker, String status,
      Instant startedAtFrom, Instant startedAtTo, Long idAfter, String cursor,
      Integer size, String sortField, String sortDirection) {
    if (cursor != null) {
      try {
        idAfter = decodeCursor(cursor);
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid cursor format");
      }
    }

    Specification<Backup> spec = getSpec(worker, status, startedAtFrom, startedAtTo, idAfter);
    Pageable pageable = createPageable(size, sortField, sortDirection, "startedAt", "desc");

    Page<Backup> backups;
    if (spec == null) {
      backups = backupRepository.findAll(pageable);
    } else {
      backups = backupRepository.findAll(spec, pageable);
    }

    List<BackupDto> dtoList = backups.getContent()
        .stream()
        .map(backupMapper::toDto)
        .toList();

    Long nextIdAfter = null;
    String nextCursor = null;

    if (!dtoList.isEmpty()) {
      nextIdAfter = dtoList.get(dtoList.size() - 1).id(); // BackupDto의 id()
      nextCursor = encodeCursor(nextIdAfter);
    }

    return new CursorPageResponseBackupDto(
        dtoList,
        nextCursor,
        nextIdAfter,
        backups.getSize(),
        backups.getTotalElements(),
        backups.hasNext()
    );
  }


  public BackupDto findLatest(String status) {
    Status backupStatus = status == null ? Status.COMPLETED : Status.valueOf(status);
    Backup lastBackup = backupRepository.findFirstByStatusOrderByStartedAtDesc(backupStatus)
        .orElse(null);
    if (lastBackup == null) {
      return null;
    }
    return backupMapper.toDto(lastBackup);
  }

  private boolean isChanged() {
    Instant lastBackupAt = findLatest("COMPLETED") == null ? null : findLatest("COMPLETED").startedAt();
    if (lastBackupAt == null) {
      return true;
    }
    return employeeRepository.findByUpdatedAtGreaterThan(lastBackupAt).isPresent();
  }


  private Specification<Backup> getSpec(String worker, String status, Instant startedAtFrom,
      Instant startedAtTo, Long idAfter) {
    Specification<Backup> spec = Specification.where(null);

    if (worker != null && !worker.isEmpty()) {
      spec = spec.and((root, query, cb) ->
          cb.like(root.get("worker"), "%" + worker + "%"));
    }
    if (status != null && !status.isEmpty()) {
      spec = spec.and((root, query, cb) ->
          cb.equal(root.get("status"), status));
    }

    if (startedAtFrom != null && startedAtTo == null) {
      spec = spec.and((root, query, cb) ->
          cb.between(root.get("startTime"), startedAtFrom, Instant.now()));
    } else if (startedAtFrom == null && startedAtTo != null) {
      spec = spec.and((root, query, cb) ->
          cb.between(root.get("startTime"), Instant.EPOCH, startedAtTo));
    }
    if (startedAtFrom != null && startedAtTo != null) {
      spec = spec.and((root, query, cb) ->
          cb.between(root.get("startTime"), startedAtFrom, startedAtTo));
    }

    if (idAfter != null) {
      spec = spec.and((root, query, criteriaBuilder) ->
          criteriaBuilder.greaterThan(root.get("id"), idAfter));
    }

    return spec;
  }

}