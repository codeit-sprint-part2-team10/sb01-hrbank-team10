package com.sprint.example.sb01part2hrbankteam10.service;

import com.sprint.example.sb01part2hrbankteam10.dto.ChangeLogDto;
import com.sprint.example.sb01part2hrbankteam10.dto.CursorPageResponseChangeLogDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10.entity.EmployeeHistory;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;

public interface EmployeeHistoryService {

    ChangeLogDto create(String employeeNumber, EmployeeHistory.ChangeType type,
                     String memo, EmployeeDto beforeData,
                        EmployeeDto afterData,
                     HttpServletRequest request);

    CursorPageResponseChangeLogDto getEmployeeHistoriesByCursor(
            String employeeNumber,
            String type,
            String memo,
            String ipAddress,
            LocalDateTime atFrom,
            LocalDateTime atTo,
            Integer idAfter,
            int size,
            String sortField,
            String sortDirection
    );

}
