package com.sprint.example.sb01part2hrbankteam10.mapper;

import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

  public static EmployeeDto toDto(Employee employee) {
    return EmployeeDto.builder()
        .id(employee.getId())
        .name(employee.getName())
        .email(employee.getEmail())
        .employeeNumber(employee.getEmployeeNumber())
        .status(employee.getStatus())
        .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
        .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null)
        .position(employee.getPosition())
        .hireDate(employee.getHireDate() != null ? employee.getHireDate().toLocalDate() : null)
        .profileImageId(employee.getProfileImage() != null ? employee.getProfileImage().getId() : null)
        .build();
  }

}
