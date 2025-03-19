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
  private Integer count;
  private Double percentage;
}
