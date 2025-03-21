package com.practice.hrbank.mapper;

import com.practice.hrbank.dto.backup.BackupDto;
import com.practice.hrbank.entity.Backup;
import org.springframework.stereotype.Component;

@Component
public class BackupMapper {

  public BackupDto toDto(Backup backup) {
    return new BackupDto(backup.getId(),
        backup.getWorker(),
        backup.getStartedAt(),
        backup.getEndedAt() == null ? null : backup.getEndedAt(),
        backup.getStatus(),
        backup.getFile() == null ? null : backup.getFile().getId());
  }
}
