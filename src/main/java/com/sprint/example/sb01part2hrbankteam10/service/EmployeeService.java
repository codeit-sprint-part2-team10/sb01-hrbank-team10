package com.sprint.example.sb01part2hrbankteam10.service;

import com.sprint.example.sb01part2hrbankteam10.dto.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeCreateRequest;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeUpdateRequest;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee.EmployeeStatus;
import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {

  EmployeeDto create(EmployeeCreateRequest request, MultipartFile profile, String clientIp);

  EmployeeDto update(Integer id, EmployeeUpdateRequest request, MultipartFile profile,
      String clientIp);

  String deleteById(Integer id);

  EmployeeDto getById(Integer id);

  CursorPageResponseDto<EmployeeDto> search(
      String nameOrEmail, String employeeNumber, String departmentName, String position,
      LocalDate hireDateFrom, LocalDate hireDateTo, EmployeeStatus status, Integer idAfter,
      String cursor, Integer size, Integer sortField, String sortDirection
  );
}
