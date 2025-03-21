package com.practice.hrbank.dto.backup;

import java.util.List;

public record CursorPageResponseBackupDto(
    List<BackupDto> content,
    String nextCursor,
    Long nextIdAfter,
    Integer size,
    Long totalElements,
    boolean hasNext
) {

}
