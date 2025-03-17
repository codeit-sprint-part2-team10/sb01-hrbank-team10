package com.sprint.example.sb01part2hrbankteam10.controller;

import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeCreateRequest;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10.global.response.RestApiResponse;
import com.sprint.example.sb01part2hrbankteam10.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

  private final EmployeeService employeeService;

  @PostMapping
  public ResponseEntity<RestApiResponse<EmployeeDto>> createEmployee (
      @Valid @RequestPart(name = "employee")EmployeeCreateRequest request,
      @RequestPart(name = "profile", required = false) MultipartFile profile
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(RestApiResponse.success(
            HttpStatus.CREATED,
            employeeService.create(request, profile)
        ));
  }
}
