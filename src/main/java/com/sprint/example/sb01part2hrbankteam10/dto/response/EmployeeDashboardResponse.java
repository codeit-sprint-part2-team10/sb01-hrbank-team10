package com.sprint.example.sb01part2hrbankteam10.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDashboardResponse {
  private int totalEmployees;
  private int recentUpdates;
  private int thisMonthHires;
  private int lastBackupCount;
}
