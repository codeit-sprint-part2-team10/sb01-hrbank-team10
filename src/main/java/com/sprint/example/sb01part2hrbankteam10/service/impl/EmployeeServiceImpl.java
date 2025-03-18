package com.sprint.example.sb01part2hrbankteam10.service.impl;

import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeCreateRequest;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeUpdateRequest;
import com.sprint.example.sb01part2hrbankteam10.entity.Department;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee.EmployeeStatus;
import com.sprint.example.sb01part2hrbankteam10.entity.EmployeeHistory;
import com.sprint.example.sb01part2hrbankteam10.entity.EmployeeHistory.ChangeType;
import com.sprint.example.sb01part2hrbankteam10.entity.File;
import com.sprint.example.sb01part2hrbankteam10.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.EmployeeErrorCode;
import com.sprint.example.sb01part2hrbankteam10.mapper.EmployeeMapper;
import com.sprint.example.sb01part2hrbankteam10.repository.DepartmentRepository;
import com.sprint.example.sb01part2hrbankteam10.repository.EmployeeRepository;
import com.sprint.example.sb01part2hrbankteam10.repository.FileRepository;
import com.sprint.example.sb01part2hrbankteam10.service.EmployeeService;
import jakarta.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final FileRepository fileRepository;
  private final DepartmentRepository departmentRepository;

  @Override
  @Transactional
  public EmployeeDto create(EmployeeCreateRequest request, MultipartFile profile, String clientIp) {

    // 이메일 중복 검사 및 에러 처리
    validateEmail(request.getEmail());

    // 부서 확인 (에러 코드 수정 필요)
    Department department = getDepartmentOrThrow(request.getDepartmentId());

    // 날짜 parsing 및 에러 처리
    LocalDateTime hireDate = parseLocalDateTime(request.getHireDate());

    // 사원 번호 생성
    String employeeNumber = generateEmployeeNumber(hireDate);

    // 이미지가 존재하면 저장
    File newProfile = null;
    if (validateFile(profile)) {
      newProfile = fileRepository.save(
          File.builder()
              .name(profile.getName())
              .contentType(profile.getContentType())
              .size(BigInteger.valueOf(profile.getSize()))
              .build()
      );
      // saveProfile(profile.bytes()) + 파싱 에러 처리
    }

    // 유저 저장
    Employee newEmployee = Employee.builder()
        .name(request.getName())
        .email(request.getEmail())
        .employeeNumber(employeeNumber)
        .position(request.getPosition())
        .hireDate(hireDate)
        .department(department)
        .profileImage(newProfile)
        .status(EmployeeStatus.ACTIVE)
        .build();

    EmployeeDto employeeDto = EmployeeMapper.toDto(employeeRepository.save(newEmployee));

    // 이력 정보 넘기
    //EmployeeHistoryService.create(ChangeType.CREATED, request.getMemo(), null, employeeDto, clientIp);

    return employeeDto;
  }

  @Transactional
  @Override
  public EmployeeDto update(Integer id, EmployeeUpdateRequest request, MultipartFile profile) {

    // 이메일 중복 검사 및 에러 처리
    validateEmail(request.getEmail());

    // 부서 확인 (에러 코드 수정 필요)
    Department department = getDepartmentOrThrow(request.getDepartmentId());

    // 날짜 parsing 및 에러 처리
    LocalDateTime hireDate = parseLocalDateTime(request.getHireDate());

    // 사원 번호 생성
    String employeeNumber = generateEmployeeNumber(hireDate);

    // 이미지가 존재하면 저장
    File newProfile = null;
    if (validateFile(profile)) {
      // 저장 로직
      newProfile = fileRepository.save(
          File.builder()
              .name(profile.getName())
              .contentType(profile.getContentType())
              .size(BigInteger.valueOf(profile.getSize()))
              .build()
      );
      // saveFile(profile.bytes()) + 파싱 에러 처리
    }

    // 유저 저장
    Employee newEmployee = Employee.builder()
        .name(request.getName())
        .email(request.getEmail())
        .employeeNumber(employeeNumber)
        .hireDate(hireDate)
        .department(null)
        .profileImage(newProfile)
        .status(EmployeeStatus.ACTIVE)
        .build();

    // 저장 이력 정보 넘기기
    // request.memo

    return EmployeeMapper.toDto(employeeRepository.save(newEmployee)); // mapper 로 변경
  }


  private boolean validateFile(MultipartFile multipartFile) {
    return multipartFile != null && !multipartFile.isEmpty();
  }

  private void validateEmail(String email) {
    if (employeeRepository.existsByEmail(email)) {
      throw new RestApiException(EmployeeErrorCode.EMAIL_IS_ALREADY_EXIST,
          "email=" + email);
    }
  }

  private Employee getByIdOrThrow(Integer id) {
    return employeeRepository.findById(id).orElseThrow(() ->
        new RestApiException(EmployeeErrorCode.EMPLOYEE_NOT_FOUND, "id=" + id));
  }

  private Department getDepartmentOrThrow(Integer departmentId) {
    return departmentRepository.findById(departmentId)
        .orElseThrow(() -> new RestApiException(EmployeeErrorCode.EMPLOYEE_NOT_FOUND,
            "departmentId=" + departmentId));
  }

  private String generateEmployeeNumber(LocalDateTime localDateTime) {
    Integer previousId = employeeRepository.findTopByOrderByIdDesc()
        .map(Employee::getId)
        .orElse(0);

    Integer footerNumber = (previousId + 1) % 1000;

    return String.format("EMP-%d-%03d", localDateTime.getYear(), footerNumber);
  }

  private LocalDateTime parseLocalDateTime(String dateString) {
    try {
      return LocalDate.parse(dateString).atStartOfDay();
    } catch (DateTimeParseException e) {
      throw new RestApiException(EmployeeErrorCode.INVALID_DATE, "hireDate=" + dateString);
    }
  }
}
