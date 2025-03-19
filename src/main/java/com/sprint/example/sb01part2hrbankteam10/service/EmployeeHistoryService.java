package com.sprint.example.sb01part2hrbankteam10.service;

import com.sprint.example.sb01part2hrbankteam10.dto.*;
import com.sprint.example.sb01part2hrbankteam10.entity.EmployeeHistory;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface EmployeeHistoryService {

    ChangeLogDto create(String employeeNumber, EmployeeHistory.ChangeType type,
                        String memo, EmployeeCreateRequest changes, EmployeeDto afterData, String clientIp);

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

    List<DiffDto> getChangeDiffs(Integer id);

    Long countEmployeeHistories(String employeeNumber,
                                String type,
                                String memo,
                                String ipAddress,
                                LocalDateTime atFrom,
                                LocalDateTime atTo,
                                Integer idAfter,
                                int size,
                                String sortField,
                                String sortDirection);

}
