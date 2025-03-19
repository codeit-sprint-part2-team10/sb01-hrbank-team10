package com.sprint.example.sb01part2hrbankteam10.dto;

import com.sprint.example.sb01part2hrbankteam10.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeForBackupDto {
    private Integer id;
    private String name;
    private String email;
    private String employeeNumber;
    private String departmentName;
    private String position;
    private LocalDate hireDate;
    private Employee.EmployeeStatus status;
}
