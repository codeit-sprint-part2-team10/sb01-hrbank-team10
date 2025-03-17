package com.sprint.example.sb01part2hrbankteam10.mapper;

import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentDto;
import com.sprint.example.sb01part2hrbankteam10.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

  public DepartmentDto toDto(Department department) {
    return DepartmentDto.builder()
        .id(department.getId())
        .name(department.getName())
        .description(department.getDescription())
        .establishedDate(department.getEstablishedDate())
//        .employeeCount(department.getEmployees().size()) //employees가 없어서 주석처리
        .build();
  }
}
