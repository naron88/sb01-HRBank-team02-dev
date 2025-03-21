package com.practice.hrbank.dto.changeLog;

import java.util.List;

public record CursorPageResponseChangeLogDto(
        List<ChangeLogDto> content,
        String nextCursor,
        String nextIdAfter,
        Integer size,
        Integer totalElements,
        boolean hasNext
) { }
