package com.sprint.example.sb01part2hrbankteam10.service;

import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDistributionDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeTrendDto;
import com.sprint.example.sb01part2hrbankteam10.dto.response.EmployeeDashboardResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EmployeeStatService {
  List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, String status);

  List<EmployeeTrendDto> getEmployeeTrend(LocalDateTime from, LocalDateTime to, String unit);

  EmployeeDashboardResponse getEmployeeDashboard(String status, LocalDate fromDate, LocalDate toDate);
}
