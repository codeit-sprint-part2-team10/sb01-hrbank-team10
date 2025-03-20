package com.sprint.example.sb01part2hrbankteam10.controller;

import com.sprint.example.sb01part2hrbankteam10.dto.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentCreateRequest;
import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentDto;
import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentResponseDto;
import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentUpdateRequest;
import com.sprint.example.sb01part2hrbankteam10.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(department);
  }

  // 부서 수정
  @PatchMapping("/{id}")
  public ResponseEntity<DepartmentDto> updateDepartment(
      @PathVariable Integer id,
      @Valid @RequestBody DepartmentUpdateRequest request) {

    DepartmentDto updatedDepartment = departmentService.update(id, request);
    return ResponseEntity.ok()
        .body(updatedDepartment);
  }

  // 부서 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteDepartment(@PathVariable Integer id) {
    return ResponseEntity.ok()
        .body(departmentService.delete(id));
  }

  // 부서 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<DepartmentDto> getDepartment(@PathVariable Integer id) {
    DepartmentDto department = departmentService.find(id);

    return ResponseEntity.ok()
    .body(department);
  }

  // 부서 목록 조회
  @GetMapping
  public ResponseEntity<CursorPageResponseDto<DepartmentResponseDto>> getDepartments(
      @RequestParam(required = false) String nameOrDescription,
      @RequestParam(required = false) Integer idAfter,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "establishedDate") String sortField,
      @RequestParam(defaultValue = "asc") String sortDirection) {

    CursorPageResponseDto<DepartmentResponseDto> response = departmentService.getDepartments(
        nameOrDescription,
        idAfter,
        cursor,
        size,
        sortField,
        sortDirection
    );

    return ResponseEntity.ok()
        .body(response);
  }
}