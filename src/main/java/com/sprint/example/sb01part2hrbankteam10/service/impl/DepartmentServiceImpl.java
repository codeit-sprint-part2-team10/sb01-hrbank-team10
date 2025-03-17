package com.sprint.example.sb01part2hrbankteam10.service.impl;

import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentCreateRequest;
import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentDto;
import com.sprint.example.sb01part2hrbankteam10.entity.Department;
import com.sprint.example.sb01part2hrbankteam10.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.DepartmentErrorCode;
import com.sprint.example.sb01part2hrbankteam10.mapper.DepartmentMapper;
import com.sprint.example.sb01part2hrbankteam10.repository.DepartmentRepository;
import com.sprint.example.sb01part2hrbankteam10.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final DepartmentMapper departmentMapper;

  @Transactional
  @Override
  public DepartmentDto create(DepartmentCreateRequest request) {

    if (departmentRepository.existsByName(request.getName())) {
      throw new RestApiException(DepartmentErrorCode.DEPARTMENT_IS_ALREADY_EXIST, request.getName());
    }

    Department department = Department.builder()
            .name(request.getName())
            .description(request.getDescription())
            .establishedDate(request.getEstablishedDate())
            .build();

    Department saved = departmentRepository.save(department);
    return departmentMapper.toDto(saved);
  }

  @Transactional
  @Override
  public DepartmentDto update(Integer id, DepartmentCreateRequest request) {
    Department findDepartment = departmentRepository.findById(id)
        .orElseThrow(() -> new RestApiException(DepartmentErrorCode.DEPARTMENT_NOT_EXIST,
            id.toString()));

    findDepartment.setName(request.getName());
    findDepartment.setDescription(request.getDescription());
    findDepartment.setEstablishedDate(request.getEstablishedDate());

    Department updatedDepartment = departmentRepository.save(findDepartment);
    return departmentMapper.toDto(updatedDepartment);
  }
}
