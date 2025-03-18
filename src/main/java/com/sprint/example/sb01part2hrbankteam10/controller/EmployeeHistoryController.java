package com.sprint.example.sb01part2hrbankteam10.controller;

import com.sprint.example.sb01part2hrbankteam10.dto.ChangeLogDto;
import com.sprint.example.sb01part2hrbankteam10.dto.CursorPageResponseChangeLogDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeHistoryCreateRequest;
import com.sprint.example.sb01part2hrbankteam10.service.EmployeeHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/change-logs")
@RequiredArgsConstructor
public class EmployeeHistoryController {

    private final EmployeeHistoryService employeeHistoryService;

    @PostMapping
    public ResponseEntity<ChangeLogDto> create(@RequestBody EmployeeHistoryCreateRequest request, HttpServletRequest httpRequest) {
        ChangeLogDto savedHistory = employeeHistoryService.create(
                request.getEmployeeNumber(),
                request.getType(),
                request.getMemo(),
                request.getBeforeData(),
                request.getAfterData(),
                httpRequest
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(savedHistory);
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseChangeLogDto<ChangeLogDto>> getEmployeeHistories(
            @RequestParam(required = false) String employeeNumber,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String memo,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime atFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime atTo,
            @RequestParam(required = false) Integer idAfter,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "at") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        CursorPageResponseChangeLogDto<ChangeLogDto> response =
                employeeHistoryService.getEmployeeHistoriesByCursor(
                        employeeNumber, type, memo, ipAddress, atFrom, atTo, idAfter, size, sortField, sortDirection);

        return ResponseEntity.ok(response);
    }
}
