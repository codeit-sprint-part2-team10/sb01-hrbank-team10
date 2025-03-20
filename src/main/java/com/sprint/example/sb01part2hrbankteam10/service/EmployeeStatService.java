package com.sprint.example.sb01part2hrbankteam10.service;

import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDistributionDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeTrendDto;
import com.sprint.example.sb01part2hrbankteam10.dto.response.EmployeeDashboardResponse;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee.EmployeeStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EmployeeStatService {
  List<EmployeeDistributionDto> getDistribution(String groupBy, EmployeeStatus status);

  List<EmployeeTrendDto> getTrend(LocalDateTime from, LocalDateTime to, String unit);

  Long getCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);
}
