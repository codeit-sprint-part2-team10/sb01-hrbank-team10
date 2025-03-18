package com.sprint.example.sb01part2hrbankteam10.service.impl;

import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentCreateRequest;
import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentDto;
import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentUpdateRequest;
import com.sprint.example.sb01part2hrbankteam10.entity.Department;
import com.sprint.example.sb01part2hrbankteam10.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.DepartmentErrorCode;
import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.EmployeeErrorCode;
import com.sprint.example.sb01part2hrbankteam10.mapper.DepartmentMapper;
import com.sprint.example.sb01part2hrbankteam10.repository.DepartmentRepository;
import com.sprint.example.sb01part2hrbankteam10.repository.EmployeeRepository;
import com.sprint.example.sb01part2hrbankteam10.service.DepartmentService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final DepartmentMapper departmentMapper;
  private final EmployeeRepository employeeRepository;

  @Transactional
  @Override
  public DepartmentDto create(DepartmentCreateRequest request) {

    LocalDateTime establishedDate = parseLocalDateTime(request.getEstablishedDate());


    if (departmentRepository.existsByName(request.getName())) {
      throw new RestApiException(DepartmentErrorCode.DEPARTMENT_IS_ALREADY_EXIST, request.getName());
    }

    Department department = Department.builder()
            .name(request.getName())
            .description(request.getDescription())
            .establishedDate(establishedDate)
            .build();

    Department saved = departmentRepository.save(department);
    return departmentMapper.toDto(saved);
  }

  @Transactional
  @Override
  public DepartmentDto update(Integer id, DepartmentUpdateRequest request) {
    Department findDepartment = departmentRepository.findById(id)
        .orElseThrow(() -> new RestApiException(DepartmentErrorCode.DEPARTMENT_NOT_EXIST,
            id.toString()));

    findDepartment.setName(request.getName());
    findDepartment.setDescription(request.getDescription());
    findDepartment.setEstablishedDate(parseLocalDateTime(request.getEstablishedDate()));

    Department updatedDepartment = departmentRepository.save(findDepartment);
    return departmentMapper.toDto(updatedDepartment);
  }

  @Transactional
  @Override
  public void delete(Integer id){
    Department findDepartment = departmentRepository.findById(id)
        .orElseThrow(() -> new RestApiException(DepartmentErrorCode.DEPARTMENT_NOT_EXIST,
            id.toString()));

    // 부서에 속한 직원이 있는 경우 삭제 불가
    if (employeeRepository.existsByDepartmentId(id)) {
      log.error("Department has employee. id={}", id);
      throw new RestApiException(DepartmentErrorCode.DEPARTMENT_HAS_EMPLOYEE, id.toString());
    }

    if (departmentRepository.existsById(id)) {
      departmentRepository.delete(findDepartment);
    }
  }


  private LocalDateTime parseLocalDateTime(String dateString) {
    try {
      return LocalDate.parse(dateString).atStartOfDay();
    } catch (DateTimeParseException e) {
      throw new RestApiException(EmployeeErrorCode.INVALID_DATE, "establishedDate=" + dateString);
    }
  }
}
