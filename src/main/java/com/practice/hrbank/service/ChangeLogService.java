package com.practice.hrbank.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.hrbank.dto.changeLog.*;
import com.practice.hrbank.dto.employee.EmployeeDto;
import com.practice.hrbank.entity.ChangeLog;
import com.practice.hrbank.repository.ChangeLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChangeLogService {

    private final ChangeLogRepository changeLogRepository;
    private final ObjectMapper objectMapper;

    public CursorPageResponseChangeLogDto getChangeLogs(ChangeLogRequestDto requestDto) {
        String sortField = (requestDto.sortField() != null) ? requestDto.sortField() : "at";
        String sortDirection = (requestDto.sortDirection() != null) ? requestDto.sortDirection() : "desc";

        // 정렬 방식 설정
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(0, requestDto.size(), Sort.by(direction, sortField));

        // 필터링을 적용하여 데이터 조회
        Page<ChangeLog> changeLogs = changeLogRepository.findByFilters(
                requestDto.employeeNumber(),
                requestDto.type(),
                requestDto.memo(),
                requestDto.ipAddress(),
                requestDto.atFrom(),
                requestDto.atTo(),
                requestDto.idAfter() != null ? requestDto.idAfter() : null,
                pageable
        );

        // ChangeLog 엔티티 -> DTO 변환
        List<ChangeLogDto> content = changeLogs.getContent().stream()
                .map(log -> new ChangeLogDto(
                        log.getId(),
                        log.getType().name(),
                        log.getEmployeeNumber(),
                        log.getMemo(),
                        log.getIpAddress(),
                        log.getAt().toString()
                ))
                .collect(Collectors.toList());

        // 다음 페이지 존재 여부 체크
        boolean hasNext = changeLogs.hasNext();
        String nextCursor = hasNext ? Base64.getEncoder().encodeToString(String.valueOf(changeLogs.getContent().get(changeLogs.getContent().size() - 1).getId()).getBytes()) : null;
        String nextIdAfter = hasNext ? String.valueOf(changeLogs.getContent().get(changeLogs.getContent().size() - 1).getId()) : null;

        return new CursorPageResponseChangeLogDto(content, nextCursor, nextIdAfter, requestDto.size(), (int) changeLogs.getTotalElements(), hasNext);
    }

    public void save(ChangeLogCreateRequest changeLogCreateRequest) {
        List<DiffDto> detail = getdiff(changeLogCreateRequest.beforeEmployeeDto(), changeLogCreateRequest.afterEmployeeDto());

        try {
            String detailJson = objectMapper.writeValueAsString(detail);

            switch(changeLogCreateRequest.changeType()) {
                case CREATED:
                    changeLogRepository.save(new ChangeLog(
                        changeLogCreateRequest.changeType(),
                        changeLogCreateRequest.afterEmployeeDto().employeeNumber(),
                        detailJson,
                        changeLogCreateRequest.memo(),
                        changeLogCreateRequest.ipAddress()));
                    break;
                default:
                    changeLogRepository.save(new ChangeLog(
                            changeLogCreateRequest.changeType(),
                            changeLogCreateRequest.beforeEmployeeDto().employeeNumber(),
                            detailJson,
                            changeLogCreateRequest.memo(),
                            changeLogCreateRequest.ipAddress()
                    ));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<DiffDto> getdiff(EmployeeDto beforeEmployeeDto, EmployeeDto afterEmployeeDto) {

        List<DiffDto> diffs = new ArrayList<>();

        // 신규 등록
        if (beforeEmployeeDto == null) {
            diffs.add(new DiffDto("입사일", null, String.valueOf(afterEmployeeDto.hireDate())));
            diffs.add(new DiffDto("이름", null, afterEmployeeDto.name()));
            diffs.add(new DiffDto("직함", null, afterEmployeeDto.position()));
            diffs.add(new DiffDto("부서명", null, afterEmployeeDto.departmentName()));
            diffs.add(new DiffDto("이메일", null, afterEmployeeDto.email()));
            diffs.add(new DiffDto("사번", null, afterEmployeeDto.employeeNumber()));
            diffs.add(new DiffDto("상태", null, String.valueOf(afterEmployeeDto.status())));
            return diffs;
        }
        // 삭제
        if (afterEmployeeDto == null) {
            diffs.add(new DiffDto("입사일", String.valueOf(beforeEmployeeDto.hireDate()), null));
            diffs.add(new DiffDto("이름", beforeEmployeeDto.name(), null));
            diffs.add(new DiffDto("직함", beforeEmployeeDto.position(), null));
            diffs.add(new DiffDto("부서명", beforeEmployeeDto.departmentName(), null));
            diffs.add(new DiffDto("이메일", beforeEmployeeDto.email(), null));
            diffs.add(new DiffDto("상태", String.valueOf(afterEmployeeDto.status()), null));
            return diffs;
        }

        // 수정
        if(afterEmployeeDto.name()!=null && !beforeEmployeeDto.name().equals(afterEmployeeDto.name())) {
            diffs.add(new DiffDto("이름", beforeEmployeeDto.name(), afterEmployeeDto.name()));
        }
        if(afterEmployeeDto.email()!= null && !beforeEmployeeDto.email().equals(afterEmployeeDto.email())) {
            diffs.add(new DiffDto("이메일", beforeEmployeeDto.email(), afterEmployeeDto.email()));
        }
        if(afterEmployeeDto.departmentName() != null && !beforeEmployeeDto.departmentName().equals(afterEmployeeDto.departmentName())) {
            diffs.add(new DiffDto("부서", beforeEmployeeDto.departmentName(), afterEmployeeDto.departmentName()));
        }
        if(afterEmployeeDto.position() != null && !beforeEmployeeDto.position().equals(afterEmployeeDto.position())) {
            diffs.add(new DiffDto("직함", beforeEmployeeDto.position(), afterEmployeeDto.position()));
        }
        if(afterEmployeeDto.hireDate() != null && !beforeEmployeeDto.hireDate().equals(afterEmployeeDto.hireDate())) {
            diffs.add(new DiffDto("입사일", String.valueOf(beforeEmployeeDto.hireDate()), String.valueOf(afterEmployeeDto.hireDate())));
        }
        if (afterEmployeeDto.status() != null && !beforeEmployeeDto.status().equals(afterEmployeeDto.status())) {
            diffs.add(new DiffDto("상태", String.valueOf(beforeEmployeeDto.status()), String.valueOf(afterEmployeeDto.status())));
        }
        return diffs;
    }

    public List<DiffDto> getDiffs(Long id) {
        ChangeLog changeLog = changeLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException());

        try {
            return objectMapper.readValue(changeLog.getDetail(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, DiffDto.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }

    public Long getChangeLogCount(String fromDate, String toDate) {
        // 기본값 설정 (최근 7일)
        LocalDateTime from = (fromDate != null) ? LocalDateTime.parse(fromDate) : LocalDateTime.now().minusDays(7);
        LocalDateTime to = (toDate != null) ? LocalDateTime.parse(toDate) : LocalDateTime.now();

        return changeLogRepository.countByAtBetween(from, to);
    }
}
