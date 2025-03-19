package com.sprint.example.sb01part2hrbankteam10.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTrendDto {

  private String date;
  private Integer count;
  private Integer change;
  private Double changeRate;

}
