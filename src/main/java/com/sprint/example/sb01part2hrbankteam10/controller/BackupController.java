package com.sprint.example.sb01part2hrbankteam10.controller;

import com.sprint.example.sb01part2hrbankteam10.controller.docs.BackupDocs;
import com.sprint.example.sb01part2hrbankteam10.dto.BackupDto;
import com.sprint.example.sb01part2hrbankteam10.dto.CursorPageResponseBackupDto;
import com.sprint.example.sb01part2hrbankteam10.entity.Backup;
import com.sprint.example.sb01part2hrbankteam10.mapper.BackupMapper;
import com.sprint.example.sb01part2hrbankteam10.repository.BackupRepository;
import com.sprint.example.sb01part2hrbankteam10.service.BackupService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("api/backups")
@RequiredArgsConstructor
public class BackupController implements BackupDocs {

    private final BackupService backupService;
    private final BackupRepository backupRepository;

    // 백업 요청
    @PostMapping
    @Override
    public ResponseEntity<Integer> backup(HttpServletRequest request) {
        Integer backupId = backupService.performBackup();
        return ResponseEntity.status(HttpStatus.OK).body(backupId);
    }

    // 백업 상태로 가장 최근 백업 얻기
    @GetMapping("/latest")
    @Override
    public ResponseEntity<BackupDto> getLastBackup(@RequestParam Backup.BackupStatus status){
        Backup lastBackup = backupRepository.findFirstByStatusOrderByStartedAtDesc(status);
        BackupDto lastBackupDto = BackupMapper.toDto(lastBackup);
        return ResponseEntity.status(HttpStatus.OK).body(lastBackupDto);
    }

    // 백업 목록 조회
    @GetMapping
    @Override
    public ResponseEntity<CursorPageResponseBackupDto> getBackupList(
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

        List<BackupDto> content = backupPage.getContent();
        String nextCursor = backupPage.hasNext() && !content.isEmpty() ?
                encodeCursor(content.get(content.size() - 1)) : null;
        Integer nextIdAfter = content.isEmpty() ? null : content.get(content.size() - 1).getId();

        CursorPageResponseBackupDto response = CursorPageResponseBackupDto.builder()
                .content(content)  // Page 객체가 아닌 content 리스트를 전달
                .nextCursor(nextCursor)
                .nextIdAfter(nextIdAfter)
                .size(backupPage.getSize())
                .totalElements((int) backupPage.getTotalElements())
                .hasNext(backupPage.hasNext())
                .build();

        return ResponseEntity.ok(response);
    }
    private String encodeCursor(BackupDto backupDto) {
        if (backupDto == null || backupDto.getId() == null) {
            throw new IllegalArgumentException("Invalid cursor data");
        }
        return Base64.getEncoder().encodeToString((backupDto.getId().toString()).getBytes());
    }
}


