package com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BackupErrorCode {
  BACKUP_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "BACKUP_001", "백업 중 오류가 발생했습니다."),
  BACKUP_TO_CSV_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BACKUP_002", "백업을 CSV 파일 변환하는 중 오류가 발생했습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
