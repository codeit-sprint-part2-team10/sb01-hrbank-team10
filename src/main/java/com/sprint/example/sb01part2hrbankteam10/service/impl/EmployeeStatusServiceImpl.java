package com.sprint.example.sb01part2hrbankteam10.service.impl;

import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDistributionDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeTrendDto;
import com.sprint.example.sb01part2hrbankteam10.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.DepartmentErrorCode;
import com.sprint.example.sb01part2hrbankteam10.repository.DashboardRepository;
import com.sprint.example.sb01part2hrbankteam10.repository.EmployeeRepository;
import com.sprint.example.sb01part2hrbankteam10.service.EmployeeStatusService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeStatusServiceImpl implements EmployeeStatusService {

  private final DashboardRepository dashboardRepository;
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-DD

  @Override
  @Transactional(readOnly = true)
  public List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, String status) {
    groupBy = groupBy == null || groupBy.isEmpty() ? "departments" : groupBy;
    status = status == null || status.isEmpty() ? "ACTIVE" : status.toUpperCase();

    if (!"ACTIVE".equals(status) && !"RESIGNED".equals(status) && !"ON_LEAVE".equals(status)) {
      throw new RestApiException(DepartmentErrorCode.DEPARTMENT_STATUS_NOT_VALID, status);
    }

    List<Object[]> results = dashboardRepository.findEmployeeDistribution(groupBy, status);
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

  @Override
  @Transactional(readOnly = true)
  public List<EmployeeTrendDto> getEmployeeTrend(LocalDateTime from, LocalDateTime to, String unit) {
    if (from == null) {
      from = LocalDateTime.now().minusYears(1); // 최근 12개월 전
    }
    if (to == null) {
      to = LocalDateTime.now(); // 현재
    }
    unit = unit == null || unit.isEmpty() ? "month" : unit.toLowerCase();

    if (!List.of("day", "week", "month", "quarter", "year").contains(unit)) {
      throw new RestApiException(DepartmentErrorCode.DEPARTMENT_STATUS_NOT_VALID, unit);
    }

    List<Object[]> trends = dashboardRepository.findEmployeeTrend(from, to, unit);

    // Object[]를 EmployeeTrendDto로 변환 및 change, changeRate 계산
    List<EmployeeTrendDto> result = new ArrayList<>();
    for (int i = 0; i < trends.size(); i++) {
      Object[] row = trends.get(i);
      String dateStr = (String) row[0]; // period
      Integer count = ((Number) row[1]).intValue(); // count
      Integer change = (i > 0) ? (count - ((Number) trends.get(i - 1)[1]).intValue()) : 0;
      Double changeRate = (i > 0 && ((Number) trends.get(i - 1)[1]).intValue() != 0)
          ? round(((double) (count - ((Number) trends.get(i - 1)[1]).intValue()) * 100) / ((Number) trends.get(i - 1)[1]).intValue(), 2)
          : 0.0;

      result.add(new EmployeeTrendDto(
          dateStr,
          count,
          change,
          changeRate
      ));
    }

    return result;
  }

  private Double round(Double value, int places) {
    if (value == null) return 0.0;
    double scale = Math.pow(10, places);
    return Math.round(value * scale) / scale;
  }
}
