package com.sprint.example.sb01part2hrbankteam10.service.impl;

import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDistributionDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeTrendDto;
import com.sprint.example.sb01part2hrbankteam10.dto.response.EmployeeDashboardResponse;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee.EmployeeStatus;
import com.sprint.example.sb01part2hrbankteam10.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.DepartmentErrorCode;
import com.sprint.example.sb01part2hrbankteam10.repository.BackupRepository;
import com.sprint.example.sb01part2hrbankteam10.repository.DashboardRepository;
import com.sprint.example.sb01part2hrbankteam10.repository.EmployeeRepository;
import com.sprint.example.sb01part2hrbankteam10.repository.specification.EmployeeSpecification;
import com.sprint.example.sb01part2hrbankteam10.service.EmployeeStatService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeStatServiceImpl implements EmployeeStatService {

  private final EmployeeRepository employeeRepository;
  private final DashboardRepository dashboardRepository;
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-DD
  private final BackupRepository backupRepository;

  @Override
  @Transactional(readOnly = true)
  public List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, EmployeeStatus status) {

    if (groupBy.equals("position")) {
      return employeeRepository.findGroupByPosition(groupBy, status);

    } else {
      return employeeRepository.findGroupByDepartment(groupBy, status);
    }

  }

  @Override
  @Transactional(readOnly = true)
  public List<EmployeeTrendDto> getEmployeeTrend(LocalDateTime from, LocalDateTime to,
      String unit) {
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
          ? round(((double) (count - ((Number) trends.get(i - 1)[1]).intValue()) * 100)
          / ((Number) trends.get(i - 1)[1]).intValue(), 2)
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

  @Override
  @Transactional(readOnly = true)
  public Long getEmployeeDashboard(EmployeeStatus status, LocalDate fromDate,
      LocalDate toDate) {

    LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
    LocalDateTime toDateTime = toDate != null ? toDate.atStartOfDay() : LocalDateTime.now();

    log.info("status: {}, fromDate: {}, toDate: {}", status, fromDateTime, toDateTime);
    Specification<Employee> specification = getCountSpecification(status, fromDateTime, toDateTime);

    return employeeRepository.count(specification);
  }


  private Specification<Employee> getCountSpecification(EmployeeStatus status, LocalDateTime fromDate,
      LocalDateTime toDate) {

    Specification<Employee> spec = ((root, query, criteriaBuilder) -> null);

    if (status != null) {
      spec = spec.and(EmployeeSpecification.equalStatus(status));
    }

    if (fromDate != null) {
      spec = spec.and(EmployeeSpecification.equalOrGreaterThanHireDateFrom(fromDate));
    }

    if (toDate != null) {
      spec = spec.and(EmployeeSpecification.lessThanHireDate(toDate));
    }

    return spec;
  }

  private Specification<Employee> getDistributionSpecification(String groupBy, EmployeeStatus status) {
    Specification<Employee> spec = ((root, query, criteriaBuilder) -> null);

    if (status != null) {
      spec = spec.and(EmployeeSpecification.equalStatus(status));
    }

    return spec;
  }

  private Double round(Double value, int places) {
    if (value == null) {
      return 0.0;
    }
    double scale = Math.pow(10, places);
    return Math.round(value * scale) / scale;
  }
}
