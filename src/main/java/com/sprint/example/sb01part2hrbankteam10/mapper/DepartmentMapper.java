package com.sprint.example.sb01part2hrbankteam10.mapper;

import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentDto;
import com.sprint.example.sb01part2hrbankteam10.entity.Department;
import com.sprint.example.sb01part2hrbankteam10.repository.EmployeeHistoryRepository;
import com.sprint.example.sb01part2hrbankteam10.repository.EmployeeRepository;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

  private final EmployeeHistoryRepository employeeHistoryRepository;
  private final EmployeeRepository employeeRepository;

  public DepartmentMapper(EmployeeHistoryRepository employeeHistoryRepository,
      EmployeeRepository employeeRepository) {
    this.employeeHistoryRepository = employeeHistoryRepository;
    this.employeeRepository = employeeRepository;
  }

  public DepartmentDto toDto(Department department) {
    return DepartmentDto.builder()
        .id(department.getId())
        .name(department.getName())
        .description(department.getDescription())
        .establishedDate(department.getEstablishedDate().toLocalDate())
        .employeeCount(employeeRepository.countByDepartmentId(department.getId())) //employees가 없어서 주석처리
        .build();
  }
}
