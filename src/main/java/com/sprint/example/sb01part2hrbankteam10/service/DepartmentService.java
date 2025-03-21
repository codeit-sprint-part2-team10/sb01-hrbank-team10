package com.sprint.example.sb01part2hrbankteam10.service;

import com.sprint.example.sb01part2hrbankteam10.dto.page.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10.dto.department.DepartmentCreateRequest;
import com.sprint.example.sb01part2hrbankteam10.dto.department.DepartmentDto;
import com.sprint.example.sb01part2hrbankteam10.dto.department.DepartmentResponseDto;
import com.sprint.example.sb01part2hrbankteam10.dto.department.DepartmentUpdateRequest;
import java.util.List;

public interface DepartmentService {

  DepartmentDto create(DepartmentCreateRequest request);

  DepartmentDto update(Integer id, DepartmentUpdateRequest request);

  String delete(Integer id);

  List<DepartmentDto> getDepartment(String name, String description);

  DepartmentDto find(Integer id);

  List<DepartmentDto> getAll();

  CursorPageResponseDto<DepartmentResponseDto> getDepartments(
      String nameOrDescription,
      Integer idAfter,
      String cursor,
      int size,
      String sortField,
      String sortDirection);
}
