package com.sprint.example.sb01part2hrbankteam10.controller;

import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeCreateRequest;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeUpdateRequest;
import com.sprint.example.sb01part2hrbankteam10.global.response.RestApiResponse;
import com.sprint.example.sb01part2hrbankteam10.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import com.sprint.example.sb01part2hrbankteam10.util.IpUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

  private final EmployeeService employeeService;

  @PostMapping
  public ResponseEntity<RestApiResponse<EmployeeDto>> createEmployee (
      HttpServletRequest httpServletRequest,
      @Valid @RequestPart(name = "employee")EmployeeCreateRequest request,
      @RequestPart(name = "profile", required = false) MultipartFile profile
  ) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(RestApiResponse.success(
            HttpStatus.CREATED,
            employeeService.create(request, profile, IpUtil.getClientIp(httpServletRequest))
        ));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<RestApiResponse<EmployeeDto>> updateEmployee (
      HttpServletRequest httpServletRequest,
      @PathVariable Integer id,
      @Valid @RequestPart(name = "employee") EmployeeUpdateRequest request,
      @RequestPart(name = "profile", required = false) MultipartFile profile
  ) {
    return ResponseEntity.ok()
        .body(RestApiResponse.success(
            HttpStatus.OK,
            employeeService.update(id, request, profile, IpUtil.getClientIp(httpServletRequest))
        ));
  }
}
