package com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode;

import com.sprint.example.sb01part2hrbankteam10.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DepartmentErrorCode implements ErrorCode {

  DEPARTMENT_NOT_EXIST(HttpStatus.NOT_FOUND, "DEPARTMENT_001", "해당 부서가 존재하지 않습니다."),
  DEPARTMENT_IS_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "DEPARTMENT_002", "이미 존재하는 부서입니다."),
  DEPARTMENT_HAS_EMPLOYEE(HttpStatus.BAD_REQUEST,"DEPARTMENT_003" ,"소속된 직원이 존재합니다." ),
  DUPLICATION_NAME(HttpStatus.BAD_REQUEST, "DEPARTMENT_004", "중복된 이름입니다." );
  // 기본 유효성 검사는 @Valid 에서 하고, 중복 데이터 검사 등은 도메인 별로 ErrorCode 를 만들어 관리합니다.

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
