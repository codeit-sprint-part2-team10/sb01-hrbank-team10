package com.sprint.example.sb01part2hrbankteam10.controller;

import com.sprint.example.sb01part2hrbankteam10.dto.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeCreateRequest;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDistributionDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeSearchRequest;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeUpdateRequest;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee.EmployeeStatus;
import com.sprint.example.sb01part2hrbankteam10.global.response.RestApiResponse;
import com.sprint.example.sb01part2hrbankteam10.service.EmployeeService;
import com.sprint.example.sb01part2hrbankteam10.service.EmployeeStatusService;
import jakarta.servlet.http.HttpServletRequest;
import com.sprint.example.sb01part2hrbankteam10.util.IpUtil;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

  private final EmployeeService employeeService;
  private final EmployeeStatusService employeeStatusService;


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
  
  @GetMapping("/{id}")
  public ResponseEntity<RestApiResponse<EmployeeDto>> getEmployee (@PathVariable Integer id) {
    return ResponseEntity.ok()
        .body(RestApiResponse.success(
            HttpStatus.OK,
            employeeService.getById(id)
        ));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<RestApiResponse<Void>> deleteEmployee (
      HttpServletRequest httpServletRequest,
      @PathVariable Integer id
  ) {
    return ResponseEntity.ok()
        .body(RestApiResponse.success(
            HttpStatus.OK,
            employeeService.deleteById(id, IpUtil.getClientIp(httpServletRequest)))
        );
  }

  @GetMapping
  public ResponseEntity<RestApiResponse<CursorPageResponseDto<EmployeeDto>>> getListEmployee(
      @RequestParam(name = "nameOrEmail", required = false) String nameOrEmail,
      @RequestParam(name = "employeeNumber", required = false) String employeeNumber,
      @RequestParam(name = "departmentName", required = false) String departmentName,
      @RequestParam(name = "position", required = false) String position,
      @RequestParam(name = "hireDateFrom", required = false) LocalDate hireDateFrom,
      @RequestParam(name = "hireDateTo", required = false) LocalDate hireDateTo,
      @RequestParam(name = "status", required = false) EmployeeStatus status,
      @RequestParam(name = "idAfter", required = false) Integer idAfter,
      @RequestParam(name = "cursor", required = false) String cursor,
      @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
      @RequestParam(name = "sortField", required = false, defaultValue = "name") String sortField,
      @RequestParam(name = "sortDirection", required = false, defaultValue = "asc") String sortDirection
  ) {
    EmployeeSearchRequest request = new EmployeeSearchRequest(
        nameOrEmail, employeeNumber, departmentName, position,
        hireDateFrom, hireDateTo, status, idAfter, cursor, size, sortField, sortDirection
    );

    return ResponseEntity.ok()
        .body(RestApiResponse.success(
            HttpStatus.OK,
            employeeService.getAllByQuery(request)
        ));
  }

  @GetMapping("/status/distribution")
  public ResponseEntity<RestApiResponse<List<EmployeeDistributionDto>>> getDistribution(
      @RequestParam(defaultValue = "department") String groupBy,
      @RequestParam(defaultValue = "ACTIVE") String Status) {

    List<EmployeeDistributionDto> distribution = employeeStatusService.getEmployeeDistribution(
        groupBy, Status);

    return ResponseEntity.status(HttpStatus.OK).body(RestApiResponse.success(HttpStatus.OK, distribution));
  }

}
