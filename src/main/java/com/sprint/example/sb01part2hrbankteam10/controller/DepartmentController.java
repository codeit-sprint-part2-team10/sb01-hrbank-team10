package com.sprint.example.sb01part2hrbankteam10.controller;

import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentCreateRequest;
import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentDto;
import com.sprint.example.sb01part2hrbankteam10.global.response.RestApiResponse;
import com.sprint.example.sb01part2hrbankteam10.service.DepartmentService;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departments")
public class DepartmentController {

  private final DepartmentService departmentService;

  // 부서 생성
  @PostMapping
  public ResponseEntity<DepartmentDto> createDepartment(
      @Valid @RequestBody DepartmentCreateRequest request) {

    DepartmentDto department = departmentService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(department);
  }

  // 부서 수정
  @PutMapping("/{id}")
  public ResponseEntity<DepartmentDto> updateDepartment(
      @PathVariable Integer id,
      @Valid @RequestBody DepartmentCreateRequest request) {

    DepartmentDto updatedDepartment = departmentService.update(id, request);
    return ResponseEntity.ok(updatedDepartment);
  }

  // 부서 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<RestApiResponse<Void>> deleteDepartment(@PathVariable Integer id) {
    departmentService.delete(id);
    return ResponseEntity.noContent().build();
  }
}