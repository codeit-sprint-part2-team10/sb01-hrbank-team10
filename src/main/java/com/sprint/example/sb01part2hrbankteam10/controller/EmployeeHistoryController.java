package com.sprint.example.sb01part2hrbankteam10.controller;

import com.sprint.example.sb01part2hrbankteam10.dto.ChangeLogDto;
import com.sprint.example.sb01part2hrbankteam10.dto.CursorPageResponseChangeLogDto;
import com.sprint.example.sb01part2hrbankteam10.dto.DiffDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeHistoryCreateRequest;
import com.sprint.example.sb01part2hrbankteam10.service.EmployeeHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.Diff;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/change-logs")
@RequiredArgsConstructor
public class EmployeeHistoryController {

    private final EmployeeHistoryService employeeHistoryService;

    // 통합한 후 삭제 예정입니다..!
    @PostMapping
    public ResponseEntity<ChangeLogDto> create(@RequestBody EmployeeHistoryCreateRequest request) {
        ChangeLogDto savedHistory = employeeHistoryService.create(
                request.getEmployeeNumber(),
                request.getType(),
                request.getMemo(),
                request.getBeforeData(),
                request.getAfterData(),
                "127.0.0.임시값"
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

    @GetMapping("/{id}/diffs")
    public ResponseEntity<List<DiffDto>> getChangeDiffs(@PathVariable Integer id) {
        List<DiffDto> diffList = employeeHistoryService.getChangeDetails(id);
        return ResponseEntity.ok(diffList);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countEmployeeHistories(
            @RequestParam(required = false) String employeeNumber,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String memo,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime atFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime atTo
    ) {
        Long count = employeeHistoryService.countEmployeeHistories(
                employeeNumber,
                type,
                memo,
                ipAddress,
                atFrom,
                atTo,
                null,
                10,
                "modifiedAt",
                "desc"
        );
        return ResponseEntity.ok(count);
    }
}
