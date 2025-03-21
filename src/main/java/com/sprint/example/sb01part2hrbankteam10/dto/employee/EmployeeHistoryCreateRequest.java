package com.sprint.example.sb01part2hrbankteam10.dto.employee;

import com.sprint.example.sb01part2hrbankteam10.entity.EmployeeHistory;
import lombok.Data;

@Data
public class EmployeeHistoryCreateRequest {

    private String employeeNumber;
    private EmployeeHistory.ChangeType type;
    private String memo;
    private EmployeeDto beforeData;
    private EmployeeDto afterData;
}
