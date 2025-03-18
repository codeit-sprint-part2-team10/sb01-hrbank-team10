package com.sprint.example.sb01part2hrbankteam10.service;

import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeCreateRequest;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {
  EmployeeDto create(EmployeeCreateRequest request, MultipartFile profile);

  EmployeeDto update(Integer id, EmployeeUpdateRequest request, MultipartFile profile);
  EmployeeDto create(EmployeeCreateRequest request, MultipartFile profile, String clientIp);
}
