package com.practice.hrbank.controller;

import com.practice.hrbank.dto.changeLog.ChangeLogRequestDto;
import com.practice.hrbank.dto.changeLog.CursorPageResponseChangeLogDto;
import com.practice.hrbank.dto.changeLog.DiffDto;
import com.practice.hrbank.service.ChangeLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/change-logs")
public class ChangeLogController {

    private final ChangeLogService changeLogsService;

    @Operation(summary = "Get 메서드 예제", description = "기본 전체 조회")
    @GetMapping()
    public ResponseEntity<CursorPageResponseChangeLogDto> search(
            @RequestParam(value = "employeeNumber", required = false) String employeeNumber,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "memo", required = false) String memo,
            @RequestParam(value = "ipAddress", required = false) String ipAddress,
            @RequestParam(value = "atFrom", required = false) String atFrom,
            @RequestParam(value = "atTo", required = false) String atTo,
            @RequestParam(value = "idAfter", required = false) Long idAfter,
            @RequestParam(value = "cursor", required = false) String cursor,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sortField", defaultValue = "at") String sortField,
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection) {

        ChangeLogRequestDto changeLogRequestDto = new ChangeLogRequestDto(
                employeeNumber, type, memo, ipAddress, atFrom, atTo,
                idAfter != null ? idAfter : null,
                cursor,
                size,
                sortField,
                sortDirection
        );

        CursorPageResponseChangeLogDto changeLogs = changeLogsService.getChangeLogs(changeLogRequestDto);
        return ResponseEntity.ok(changeLogs);

    }

    @GetMapping("/{id}/diffs")
    public ResponseEntity<List<DiffDto>> diffs(@PathVariable("id") Long id) {
        List<DiffDto> diffs = changeLogsService.getDiffs(id);
        return ResponseEntity.ok(diffs);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        Long count = changeLogsService.getChangeLogCount(fromDate, toDate);
        return ResponseEntity.ok(count);
    }
}
