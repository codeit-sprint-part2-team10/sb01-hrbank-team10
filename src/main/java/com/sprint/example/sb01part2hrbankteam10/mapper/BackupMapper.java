package com.sprint.example.sb01part2hrbankteam10.mapper;

import com.sprint.example.sb01part2hrbankteam10.dto.BackupDto;
import com.sprint.example.sb01part2hrbankteam10.entity.Backup;

public class BackupMapper {
    public static BackupDto toDto(Backup backup) {
        // NPE 방지
        Integer fileId = backup.getFile() != null ? backup.getFile().getId() : null;

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