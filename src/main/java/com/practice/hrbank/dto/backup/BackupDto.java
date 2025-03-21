package com.practice.hrbank.dto.backup;

import com.practice.hrbank.entity.Backup;
import java.time.Instant;

public record BackupDto(
    Long id,
    String worker,
    Instant startedAt,
    Instant endedAt,
    Backup.Status status,
    Long fileId
) {

}
