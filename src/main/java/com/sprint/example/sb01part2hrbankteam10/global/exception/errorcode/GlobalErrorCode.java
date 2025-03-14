package com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode;

import com.sprint.example.sb01part2hrbankteam10.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode {

  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_001", "담당자에게 문의해주세요.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
