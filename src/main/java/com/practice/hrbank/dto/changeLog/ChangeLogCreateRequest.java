package com.practice.hrbank.dto.changeLog;

import com.practice.hrbank.dto.employee.EmployeeDto;
import com.practice.hrbank.entity.ChangeLog;

public record ChangeLogCreateRequest(
        EmployeeDto beforeEmployeeDto,
        EmployeeDto afterEmployeeDto,
        String ipAddress,
        String memo,
        ChangeLog.Type changeType
) {
}
