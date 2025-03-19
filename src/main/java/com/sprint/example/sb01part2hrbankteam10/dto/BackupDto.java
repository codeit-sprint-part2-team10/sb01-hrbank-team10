package com.sprint.example.sb01part2hrbankteam10.dto;

import com.sprint.example.sb01part2hrbankteam10.entity.Backup.BackupStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackupDto {
  Integer id;
  String workerIpAddress;
  LocalDateTime startedAt;
  LocalDateTime endedAt;
  BackupStatus status;
  Integer fildId;
}
