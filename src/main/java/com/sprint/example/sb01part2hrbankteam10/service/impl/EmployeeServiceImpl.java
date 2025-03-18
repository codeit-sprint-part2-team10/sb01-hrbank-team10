package com.sprint.example.sb01part2hrbankteam10.service.impl;

import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeCreateRequest;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeUpdateRequest;
import com.sprint.example.sb01part2hrbankteam10.entity.Department;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee.EmployeeStatus;
import com.sprint.example.sb01part2hrbankteam10.entity.File;
import com.sprint.example.sb01part2hrbankteam10.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.EmployeeErrorCode;
import com.sprint.example.sb01part2hrbankteam10.mapper.EmployeeMapper;
import com.sprint.example.sb01part2hrbankteam10.repository.DepartmentRepository;
import com.sprint.example.sb01part2hrbankteam10.repository.EmployeeRepository;
import com.sprint.example.sb01part2hrbankteam10.repository.FileRepository;
import com.sprint.example.sb01part2hrbankteam10.service.EmployeeService;
import com.sprint.example.sb01part2hrbankteam10.storage.FileStorage;
import jakarta.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final FileRepository fileRepository;
  private final DepartmentRepository departmentRepository;
  private final FileStorage fileStorage;

  @Override
  @Transactional
  public EmployeeDto create(EmployeeCreateRequest request, MultipartFile profile, String clientIp) {

    validateEmail(request.getEmail());

    // 부서 확인 (에러 코드 수정 필요)
    Department department = getDepartmentOrThrow(request.getDepartmentId());
    LocalDateTime hireDate = parseLocalDateTime(request.getHireDate());
    String employeeNumber = generateEmployeeNumber(hireDate);

    File newProfile = null;
    if (validateFile(profile)) {
      newProfile = fileRepository.save(
          File.builder()
              .name(profile.getName())
              .contentType(profile.getContentType())
              .size(BigInteger.valueOf(profile.getSize()))
              .build()
      );
      fileStorage.saveProfile(newProfile.getId(), profile);
    }

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

    //EmployeeHistoryService.create(ChangeType.CREATED, request.getMemo(), null, employeeDto, clientIp);

    return employeeDto;
  }

  @Transactional
  @Override
  public EmployeeDto update(Integer id, EmployeeUpdateRequest request, MultipartFile profile,
      String clientIp) {

    Employee employee = getByIdOrThrow(id);
    EmployeeDto before = EmployeeMapper.toDto(employee);

    Optional.ofNullable(request.getName()).ifPresent(employee::updateName);
    Optional.ofNullable(request.getEmail()).ifPresent(employee::updateEmail);
    Optional.ofNullable(request.getPosition()).ifPresent(employee::updatePosition);
    Optional.ofNullable(request.getStatus()).ifPresent(employee::updateStatus);
    Optional.ofNullable(request.getHireDate()).ifPresent(hireDate -> {
      employee.updateHireDate(parseLocalDateTime(hireDate));
    });
    Optional.ofNullable(request.getDepartmentId()).ifPresent(departmentId -> {
      employee.updateDepartment(getDepartmentOrThrow(departmentId));
    });

    File newProfile = null;
    if (validateFile(profile)) {
      newProfile = fileRepository.save(
          File.builder()
              .name(profile.getName())
              .contentType(profile.getContentType())
              .size(BigInteger.valueOf(profile.getSize()))
              .build()
      );
      Integer previousProfileImageId = employee.getProfileImage().getId();
      fileStorage.saveProfile(newProfile.getId(), profile); // 로컬 저장
      employee.updateProfileImage(newProfile);              // 프로필 업데이트
      fileRepository.deleteById(previousProfileImageId);    // 기존 프로필 데이터 삭제
    }

    EmployeeDto after = EmployeeMapper.toDto(employee);

    // EmployeeHistoryService.create(ChangeType.UPDATE, request.getMemo(), before, after, clientIp);

    return after;
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
