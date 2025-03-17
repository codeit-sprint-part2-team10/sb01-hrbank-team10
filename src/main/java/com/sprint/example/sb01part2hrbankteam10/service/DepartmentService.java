package com.sprint.example.sb01part2hrbankteam10.service;

import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentCreateRequest;
import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentDto;
import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentUpdateRequest;
import com.sprint.example.sb01part2hrbankteam10.entity.Department;
import java.util.List;
import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentUpdateRequest;

public interface DepartmentService {

  DepartmentDto create(DepartmentCreateRequest request);

  DepartmentDto update(Integer id, DepartmentUpdateRequest request);

  void delete(Integer id);
}
