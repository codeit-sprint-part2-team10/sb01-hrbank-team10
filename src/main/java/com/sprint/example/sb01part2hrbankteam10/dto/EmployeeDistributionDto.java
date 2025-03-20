package com.sprint.example.sb01part2hrbankteam10.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDistributionDto {
  private String groupKey;
  private Long count;
  private Double percentage;

//  public EmployeeDistributionDto(String groupKey, Long count) {
//    this.groupKey = groupKey;
//    this.count = count;
//  }
}
