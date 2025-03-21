package com.sprint.example.sb01part2hrbankteam10.service;

import com.sprint.example.sb01part2hrbankteam10.dto.backup.BackupDto;
import com.sprint.example.sb01part2hrbankteam10.entity.Backup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

public interface BackupService {
  Integer performBackup();
  Page<BackupDto> getBackupList(String workerIpAddress,
                               Backup.BackupStatus status,
                               LocalDateTime startedAtFrom,
                               LocalDateTime startedAtTo,
                               Integer fileId,
                               Integer idAfter,
                               String cursor,
                               int size,
                               String sortField,
                               Sort.Direction sortDirection);
}
