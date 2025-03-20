package com.sprint.example.sb01part2hrbankteam10.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTrendDto {

  private String date;
  private Long count;
  private Long change;
  private Double changeRate;

//  public EmployeeTrendDto(String date, Long count, Long change, Double changeRate) {
//    this.date = date;
//    this.count = count;
//    this.change = (change != null) ? change : 0;
//    this.changeRate = (changeRate != null) ? Math.round(changeRate * 100.0) / 100.0 : 0.0;
//  }

}
