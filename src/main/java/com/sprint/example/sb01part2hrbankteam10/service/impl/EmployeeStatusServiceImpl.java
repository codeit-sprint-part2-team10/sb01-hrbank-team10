package com.sprint.example.sb01part2hrbankteam10.service.impl;

import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDistributionDto;
import com.sprint.example.sb01part2hrbankteam10.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.DepartmentErrorCode;
import com.sprint.example.sb01part2hrbankteam10.repository.EmployeeRepository;
import com.sprint.example.sb01part2hrbankteam10.service.EmployeeStatusService;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeStatusServiceImpl implements EmployeeStatusService {

  private final EmployeeRepository employeeRepository;

  @Override
  @Transactional(readOnly = true)
  public List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, String status) {
    groupBy = groupBy == null || groupBy.isEmpty() ? "departments" : groupBy;
    status = status == null || status.isEmpty() ? "ACTIVE" : status.toUpperCase();

    if (!"ACTIVE".equals(status) && !"RESIGNED".equals(status) && !"ON_LEAVE".equals(status)) {
      throw new RestApiException(DepartmentErrorCode.DEPARTMENT_STATUS_NOT_VALID, status);
    }

    List<Object[]> results = employeeRepository.findEmployeeDistribution(groupBy, status);
    return results.stream()
        .map(row -> {
          String groupKey = (String) row[0];
          Integer count = ((Number) row[1]).intValue();
          Double percentage = ((Number) row[2]).doubleValue();
          return EmployeeDistributionDto.builder()
              .groupKey(groupKey)
              .count(count)
              .percentage(percentage)
              .build();
        })
        .sorted(Comparator.comparing(EmployeeDistributionDto::getPercentage, Comparator.reverseOrder())) // 퍼센티지 내림차순 정렬
        .collect(Collectors.toList());
  }

}
