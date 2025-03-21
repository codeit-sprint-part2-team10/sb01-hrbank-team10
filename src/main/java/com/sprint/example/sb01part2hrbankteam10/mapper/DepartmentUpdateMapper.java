package com.sprint.example.sb01part2hrbankteam10.mapper;


import com.sprint.example.sb01part2hrbankteam10.dto.department.DepartmentUpdateRequest;
import com.sprint.example.sb01part2hrbankteam10.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DepartmentUpdateMapper {
  void updateDepartmentFromRequest(DepartmentUpdateRequest request, @MappingTarget Department department);
}
