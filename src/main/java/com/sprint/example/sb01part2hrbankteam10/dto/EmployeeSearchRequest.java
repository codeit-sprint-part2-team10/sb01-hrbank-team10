package com.sprint.example.sb01part2hrbankteam10.dto;

import com.sprint.example.sb01part2hrbankteam10.entity.Employee.EmployeeStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSearchRequest {
  private String nameOrEmail;
  private String employeeNumber;
  private String departmentName;
  private String position;

  private LocalDate hireDateFrom;
  private LocalDate hireDateTo;

  private EmployeeStatus status;
  private Integer idAfter;
  private String cursor;

  private Integer size = 10;  // 기본값 설정
  private Integer sortField;
  private String sortDirection = "ASC";  // 기본값 설정
}
