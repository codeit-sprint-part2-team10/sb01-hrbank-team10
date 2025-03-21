package com.sprint.example.sb01part2hrbankteam10.mapper;

import com.sprint.example.sb01part2hrbankteam10.dto.backup.BackupDto;
import com.sprint.example.sb01part2hrbankteam10.entity.Backup;

public class BackupMapper {
    public static BackupDto toDto(Backup backup) {
        // NPE 방지
        Integer fileId = backup.getBinaryContent() != null ? backup.getBinaryContent().getId() : null;

        return BackupDto.builder()
                .id(backup.getId())
                .worker(backup.getWorkerIpAddress())
                .startedAt(backup.getStartedAt())
                .endedAt(backup.getEndedAt())
                .status(backup.getStatus())
                .fileId(fileId)
                .build();
    }
}