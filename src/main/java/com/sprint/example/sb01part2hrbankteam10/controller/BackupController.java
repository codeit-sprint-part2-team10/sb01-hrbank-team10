package com.sprint.example.sb01part2hrbankteam10.controller;

import com.sprint.example.sb01part2hrbankteam10.dto.BackupDto;
import com.sprint.example.sb01part2hrbankteam10.entity.Backup;
import com.sprint.example.sb01part2hrbankteam10.repository.BackupRepository;
import com.sprint.example.sb01part2hrbankteam10.service.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

@RestController
@RequestMapping("api/backups")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;
    private final BackupRepository backupRepository;

    // 백업 요청 TODO 에러 코드
    @PostMapping
    public ResponseEntity<Integer> Backup(@RequestParam String workerIpAddress) {
        Integer backupId = backupService.performBackup(workerIpAddress);
        return ResponseEntity.status(HttpStatus.OK).body(backupId);
    }

    // 백업 상태로 가장 최근 백업 얻기 TODO 에러 코드
    @GetMapping("/latest")
    public ResponseEntity<BackupDto> getLastBackup(Backup.BackupStatus status){
        BackupDto lastBackupDto = backupRepository.findLastBackupByStatus(status);
        return ResponseEntity.status(HttpStatus.OK).body(lastBackupDto);
    }

    // 백업 목록 조회 - TODO 에러 코드
    @GetMapping("/api/backups")
    public ResponseEntity<Map<String, Object>> getBackupList(
            @RequestParam(required = false) String worker,
            @RequestParam(required = false) Backup.BackupStatus status,
            @RequestParam(required = false) LocalDateTime startedAtFrom,
            @RequestParam(required = false) LocalDateTime startedAtTo,
            @RequestParam(required = false) Integer idAfter,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startedAt") String sortField,
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection) {

        Pageable pageable = PageRequest.of(0, size, Sort.by(sortDirection, sortField));
        Page<BackupDto> backupPage = backupService.getBackupList(worker, status, startedAtFrom, startedAtTo, idAfter, null, size, sortField, sortDirection);

        Map<String, Object> response = new HashMap<>();
        response.put("content", backupPage.getContent());
        response.put("nextCursor", backupPage.hasNext() ? encodeCursor(backupPage.getContent().get(backupPage.getContent().size() - 1)) : null);
        response.put("nextIdAfter", backupPage.getContent().isEmpty() ? null : backupPage.getContent().get(backupPage.getContent().size() - 1).getId());
        response.put("size", backupPage.getSize());
        response.put("totalElements", backupPage.getTotalElements());
        response.put("hasNext", backupPage.hasNext());

        return ResponseEntity.ok(response);
    }

    private String encodeCursor(BackupDto backupDto) {
        if (backupDto == null || backupDto.getId() == null) {
            throw new IllegalArgumentException("Invalid cursor data");
        }
        return Base64.getEncoder().encodeToString((backupDto.getId().toString()).getBytes());
    }
}


