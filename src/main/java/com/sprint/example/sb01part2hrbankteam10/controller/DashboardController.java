package com.sprint.example.sb01part2hrbankteam10.controller;

import com.sprint.example.sb01part2hrbankteam10.dto.response.EmployeeDashboardResponse;
import com.sprint.example.sb01part2hrbankteam10.global.response.RestApiResponse;
import com.sprint.example.sb01part2hrbankteam10.service.EmployeeService;
import com.sprint.example.sb01part2hrbankteam10.service.EmployeeStatusService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class DashboardController {

  private final EmployeeStatusService employeeStatusService;

  @GetMapping("/status/dashboard")
  public ResponseEntity<RestApiResponse<EmployeeDashboardResponse>> getEmployeeDashboard(
      @RequestParam(required = false) String status,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

    EmployeeDashboardResponse response = employeeStatusService.getEmployeeDashboard(status, fromDate,
        toDate);
    return ResponseEntity.status(HttpStatus.OK)
        .body(RestApiResponse.success(HttpStatus.OK, response));

  }
}
