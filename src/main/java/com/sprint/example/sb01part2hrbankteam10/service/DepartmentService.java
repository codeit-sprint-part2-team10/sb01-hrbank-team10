package com.sprint.example.sb01part2hrbankteam10.service;

import com.sprint.example.sb01part2hrbankteam10.dto.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentCreateRequest;
import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentDto;
import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentResponseDto;
import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentUpdateRequest;
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
