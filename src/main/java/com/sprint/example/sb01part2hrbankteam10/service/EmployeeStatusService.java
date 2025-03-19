package com.sprint.example.sb01part2hrbankteam10.service;

import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDistributionDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeTrendDto;
import java.time.LocalDateTime;
import java.util.List;

public interface  EmployeeStatusService {
  List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, String status);

  List<EmployeeTrendDto> getEmployeeTrend(LocalDateTime from, LocalDateTime to, String unit);
}
