package com.sprint.example.sb01part2hrbankteam10.service.impl;

import com.sprint.example.sb01part2hrbankteam10.dto.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentCreateRequest;
import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentDto;
import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentResponseDto;
import com.sprint.example.sb01part2hrbankteam10.dto.DepartmentUpdateRequest;
import com.sprint.example.sb01part2hrbankteam10.entity.Department;
import com.sprint.example.sb01part2hrbankteam10.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.DepartmentErrorCode;
import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.EmployeeErrorCode;
import com.sprint.example.sb01part2hrbankteam10.mapper.DepartmentMapper;
import com.sprint.example.sb01part2hrbankteam10.mapper.DepartmentUpdateMapper;
import com.sprint.example.sb01part2hrbankteam10.repository.DepartmentRepository;
import com.sprint.example.sb01part2hrbankteam10.repository.EmployeeRepository;
import com.sprint.example.sb01part2hrbankteam10.service.DepartmentService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
  private final DepartmentUpdateMapper departmentUpdateMapper;

  @Transactional
  @Override
  public DepartmentDto create(DepartmentCreateRequest request) {

    LocalDateTime establishedDate = parseLocalDateTime(request.getEstablishedDate());

    if (departmentRepository.existsByName(request.getName())) {
      throw new RestApiException(DepartmentErrorCode.DEPARTMENT_IS_ALREADY_EXIST,
          request.getName());
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
        .orElseThrow(() -> new RestApiException(DepartmentErrorCode.DEPARTMENT_NOT_FOUND,
            id.toString()));

    Optional.ofNullable(request.getName()).ifPresent(name -> {
      if (!name.equals(findDepartment.getName()) && departmentRepository.existsByName(name)) {
        throw new RestApiException(DepartmentErrorCode.DUPLICATION_NAME, name);
      }
    });

    departmentUpdateMapper.updateDepartmentFromRequest(request, findDepartment);

    Department savedDepartment = departmentRepository.save(findDepartment);
    return departmentMapper.toDto(savedDepartment);
  }

  @Transactional
  @Override
  public String delete(Integer id) {
    Department findDepartment = departmentRepository.findById(id)
        .orElseThrow(() -> new RestApiException(DepartmentErrorCode.DEPARTMENT_NOT_FOUND,
            id.toString()));

    // 부서에 속한 직원이 있는 경우 삭제 불가
    if (employeeRepository.existsByDepartmentId(id)) {
      log.error("부서에 직원이 존재합니다. id={}", id);
      throw new RestApiException(DepartmentErrorCode.DEPARTMENT_HAS_EMPLOYEE, id.toString());
    }

    if (departmentRepository.existsById(id)) {
      departmentRepository.delete(findDepartment);
    }
    return "부서가 성공적으로 삭제되었습니다.";
  }

  @Override
  public List<DepartmentDto> getDepartment(String name, String description) {
    List<Department> departments = departmentRepository.findByNameAndDescription(
        name != null ? name : "",
        description != null ? description : "");

    return departments.stream()
        .map(departmentMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public DepartmentDto find(Integer id) {
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new RestApiException(DepartmentErrorCode.DEPARTMENT_NOT_FOUND,
            id.toString()));

    return departmentMapper.toDto(department);
  }

  @Override
  public List<DepartmentDto> getAll() {
    List<Department> departments = departmentRepository.findAll();

    if (departments.isEmpty()) {
      throw new RestApiException(DepartmentErrorCode.DEPARTMENTS_EMPTY, "부서가 존재하지 않습니다.");
    }

    return departments.stream()
        .map(departmentMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponseDto<DepartmentResponseDto> getDepartments(
      String nameOrDescription,
      Integer idAfter,
      String cursor,
      int size,
      String sortField,
      String sortDirection) {

    // 기본값 설정
    size = size <= 0 ? 10 : size;
    sortField = sortField == null || sortField.isEmpty() ? "establishedDate" : sortField;
    sortDirection = sortDirection == null || sortDirection.isEmpty() ? "asc" : sortDirection;

    // 커서에서 idAfter 추출
    if (cursor != null && !cursor.isEmpty() && idAfter == null) {
      if ("name".equals(sortField)) {
        // 이름 기반 커서
        Department dept = (Department) departmentRepository.findByName(cursor)
            .orElse(null);
        if (dept != null) {
          idAfter = dept.getId();
        }
      } else if ("establishedDate".equals(sortField)) {
        // 설립일 기반 커서 - 해당 날짜 이후의 첫 번째 부서를 찾아야 함
        try {
          LocalDateTime date = LocalDateTime.parse(cursor);
          Department dept = (Department) departmentRepository.findFirstByEstablishedDateAfterOrderById(date)
              .orElse(null);
          if (dept != null) {
            idAfter = dept.getId();
          }
        } catch (Exception e) {
          // 날짜 파싱 오류 처리
          log.warn("설립일 커서 파싱 오류: {}", cursor);
        }
      } else {
        // ID 기반 커서
        try {
          idAfter = Integer.parseInt(cursor);
        } catch (NumberFormatException e) {
          log.warn("ID 커서 파싱 오류: {}", cursor);
        }
      }
    }

    // 데이터 조회 (size + 1로 조회하여 hasNext 판단)
    List<Department> departments = departmentRepository.findDepartmentsWithCursor(
        nameOrDescription,
        idAfter,
        sortField,
        sortDirection
    );

    // 전체 개수 조회
    Long totalElements = departmentRepository.countByNameContainingOrDescriptionContaining(
        nameOrDescription != null ? nameOrDescription : "",
        nameOrDescription != null ? nameOrDescription : ""
    );

    // 페이지네이션 처리
    boolean hasNext = departments.size() > size;
    List<DepartmentResponseDto> content = departments.stream()
        .limit(size)
        .map(departmentMapper::toDto) // DepartmentDto로 변환
        .map(dto -> DepartmentResponseDto.builder()
            .id(dto.getId())
            .name(dto.getName())
            .description(dto.getDescription())
            .establishedDate(dto.getEstablishedDate())
            .employeeCount(dto.getEmployeeCount())
            .build()) // DepartmentResponseDto로 변환
        .toList();

    String nextCursor = null;
    Integer nextIdAfter = null;
    if (hasNext && !content.isEmpty()) {
      Department lastItem = departments.get((int)Math.min(size, departments.size()) - 1);
      nextIdAfter = lastItem.getId();

      // 정렬 필드에 따라 커서 값을 다르게 설정
      if ("name".equals(sortField)) {
        nextCursor = lastItem.getName();
      } else if ("establishedDate".equals(sortField)) {
        nextCursor = lastItem.getEstablishedDate().toString();
      } else {
        nextCursor = null;
      }
    }

    return CursorPageResponseDto.<DepartmentResponseDto>builder()
        .content(content)
        .nextCursor(nextCursor)
        .nextIdAfter(nextIdAfter != null ? nextIdAfter.longValue() : null)
        .size(size)
        .totalElements(totalElements)
        .hasNext(hasNext)
        .build();
  }

  private LocalDateTime parseLocalDateTime(String dateString) {
    try {
      return LocalDate.parse(dateString).atStartOfDay();
    } catch (DateTimeParseException e) {
      throw new RestApiException(EmployeeErrorCode.INVALID_DATE, "establishedDate=" + dateString);
    }
  }
}
