package com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode;

import com.sprint.example.sb01part2hrbankteam10.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode {

  FILE_WRITE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_001", "파일 저장 중 오류가 발생했습니다."),
  FILE_STREAM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_002", "백업 데이터 스트리밍 중 오류가 발생했습니다."),
  FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE_003", "파일을 찾을 수 없습니다."),
  DIRECTORY_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_004", "파일 저장 디렉토리 생성에 실패했습니다."),
  FILE_READ_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_005", "파일 다운로드 중 오류가 발생했습니다."),
  FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_006", "파일 삭제 중 오류가 발생했습니다.");
  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
