package com.sprint.example.sb01part2hrbankteam10.service.impl;

import com.sprint.example.sb01part2hrbankteam10.dto.BackupDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeForBackupDto;
import com.sprint.example.sb01part2hrbankteam10.entity.Backup;
import com.sprint.example.sb01part2hrbankteam10.entity.Backup.BackupStatus;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee;
import com.sprint.example.sb01part2hrbankteam10.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.BackupErrorCode;
import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.FileErrorCode;
import com.sprint.example.sb01part2hrbankteam10.mapper.EmployeeMapper;
import com.sprint.example.sb01part2hrbankteam10.repository.BackupRepository;
import com.sprint.example.sb01part2hrbankteam10.repository.EmployeeHistoryRepository;
import com.sprint.example.sb01part2hrbankteam10.repository.EmployeeRepository;
import com.sprint.example.sb01part2hrbankteam10.repository.FileRepository;
import com.sprint.example.sb01part2hrbankteam10.service.BackupService;
import com.sprint.example.sb01part2hrbankteam10.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;

// ID,직원번호,이름,이메일,부서,직급,입사일,상태
@Service
@RequiredArgsConstructor
public class BackupServiceImpl implements BackupService {

  private final BackupRepository backupRepository;
  private final FileRepository fileRepository;
  private final FileStorage fileStorage;
  private final EmployeeRepository employeeRepository;
  private final EmployeeHistoryRepository employeeHistoryRepository;
  private final ModelMapper modelMapper;

  @Override
  public Integer performBackup(String workerIpAddress) {

    // 로직: if 백업 불필요 -> 건너뜀 상태로 배치이력 저장하고 프로세스 종료
    if (!isBackupNeeded()) {
      Backup backupHistory = createBackupHistory(workerIpAddress, BackupStatus.SKIPPED, null, null, null);
      return backupRepository.save(backupHistory).getId();
    }

    // 진행중 상태로 배치이력 저장, 작업자는 요청자의 ip 주소
    Backup atStartBackupHistory = createBackupHistory(workerIpAddress, BackupStatus.IN_PROGRESS,
            LocalDateTime.now(), null, null);
    backupRepository.save(atStartBackupHistory);

    File backupFile = null;
    // 백업
    try {
      // 백업 로직 수행 (직원 정보 가져오기)
      List<EmployeeForBackupDto> backupContent = fetchEmployeeData();

      // 전체 직원 정보를 CSV 파일로 저장 = 스트리밍/버퍼
      ResponseEntity<Resource> response = convertBackupToCsvFile(backupContent);

      // 파일을 MultipartFile로 변환 (파일이 생성되었을 경우만)
      if (response.getBody() instanceof FileSystemResource) {
        backupFile = ((FileSystemResource) response.getBody()).getFile();
      }

      // 백업 파일이 생성되지 않으면 에러 처리
      if (backupFile == null || !backupFile.exists()) {
        throw new RestApiException(BackupErrorCode.BACKUP_FILE_CREATION_FAILED, "파일 생성에 실패했습니다.");
      }

      // 파일을 MultipartFile로 변환
      MultipartFile backUpMultipartFile = convertFileToMultipartFile(backupFile);

      // 파일 정보를 먼저 DB에 저장 후 ID를 가져옴
      BigInteger fileSize = BigInteger.valueOf(backupFile.length());
      com.sprint.example.sb01part2hrbankteam10.entity.File fileEntity = com.sprint.example.sb01part2hrbankteam10.entity.File.builder()
              .name(backupFile.getName())
              .contentType("text/csv")
              .size(fileSize)
              .build();

      // 저장하고 ID 반환받기
      Integer fileId = fileRepository.save(fileEntity).getId();
      com.sprint.example.sb01part2hrbankteam10.entity.File savedFile = fileRepository.findById(fileId)
              .orElseThrow(() -> new RestApiException(FileErrorCode.FILE_NOT_FOUND, "파일을 찾을 수 없습니다."));

      // 확실히 ID가 있는지 확인
      if (fileId == null) {
        throw new RestApiException(BackupErrorCode.BACKUP_FILE_CREATION_FAILED, "파일 ID를 생성할 수 없습니다.");
      }

      // 그 후에 파일 저장
      fileStorage.saveBackup(fileId, backUpMultipartFile);

      // 백업 성공 -> 백업이력 완료로 수정
      Backup completedBackupHistory = createBackupHistory(workerIpAddress, BackupStatus.COMPLETED,
              atStartBackupHistory.getStartedAt(), LocalDateTime.now(), savedFile);
      return backupRepository.save(completedBackupHistory).getId();

    } catch (Exception e) {
      // 저장하던 파일 삭제, 에러로그 .log 파일로 저장, 백업이력 실패로 수정
      MultipartFile file = logError(e);
      com.sprint.example.sb01part2hrbankteam10.entity.File savedLogFile = com.sprint.example.sb01part2hrbankteam10.entity.File.builder()
              .name(file.getName())
              .contentType(file.getContentType())
              .size(BigInteger.valueOf(file.getSize()))
              .build();


      Backup failedBackupHistory = createBackupHistory(workerIpAddress, BackupStatus.FAILED,
              atStartBackupHistory.getStartedAt(), null, savedLogFile);
      backupRepository.save(failedBackupHistory);

      // 파일이 생성되었을 경우에만 삭제
      if (backupFile != null) {
        Integer fileId = fileRepository.findByName(backupFile.getName());
        if (fileId != null) {
          fileRepository.deleteById(fileId);
          fileStorage.deleteBackup(fileId);
        }
      }
      throw new RestApiException(BackupErrorCode.BACKUP_ERROR, failedBackupHistory.getId().toString());
    }
  }

  // 백업 여부 결정 로직: 가장 최근 완료된 배치 작업시간 < 직원 데이터 변경 시간 -> 백업 필요
  private boolean isBackupNeeded() {

    String dataString1 = "2025-03-11T:00:00:00";
    LocalDateTime lastBackupTime = LocalDateTime.parse(dataString1);
//    LocalDateTime lastBackupTime = backupRepository.findLastCompletedBackupAt();

    if (lastBackupTime == null) {
      lastBackupTime = LocalDateTime.MIN;
    }
    String dataString2 = "2025-03-12T:00:00:00";
    LocalDateTime lastEmployeeUpdate = LocalDateTime.parse(dataString2);
//    LocalDateTime lastEmployeeUpdate = employeeHistoryRepository.findLastModifiedAt();
    if (lastEmployeeUpdate == null) {
      lastEmployeeUpdate = LocalDateTime.MIN;
    }

    return lastBackupTime.isBefore(lastEmployeeUpdate);
  }

  private Backup createBackupHistory(String workerIpAddress, BackupStatus status, LocalDateTime startedAt, LocalDateTime endedAt, com.sprint.example.sb01part2hrbankteam10.entity.File file) {
    return Backup.builder()
            .workerIpAddress(workerIpAddress)
            .status(status)
            .startedAt(startedAt)
            .endedAt(endedAt)
            .file(file)
            .build();
  }

  private ResponseEntity<Resource> convertBackupToCsvFile(List<EmployeeForBackupDto> backupContent) {
    File csvFile = null;
    try {
      // 임시 디렉토리에 파일 생성
      csvFile = File.createTempFile("backup_", ".csv");

      try (BufferedWriter writer = new BufferedWriter(
              new OutputStreamWriter(new FileOutputStream(csvFile), StandardCharsets.UTF_8))) {
        // 헤더 작성
        writer.write("ID,직원번호,이름,이메일,부서,직급,입사일,상태");
        writer.newLine();

        // ID 목록 추출
        List<Integer> employeeIds = backupContent.stream()
                .map(EmployeeForBackupDto::getId)
                .collect(Collectors.toList());

        // 한 번의 쿼리로 모든 직원 정보 가져오기
        Map<Integer, Employee> employeeMap = employeeRepository.findAllById(employeeIds)
                .stream()
                .collect(Collectors.toMap(Employee::getId, employee -> employee));

        // CSV 데이터 작성
        for (EmployeeForBackupDto dto : backupContent) {
          Employee employee = employeeMap.get(dto.getId());
          if (employee != null) {
            writer.write(
                    escapeCsv(String.valueOf(employee.getId())) + "," +
                            escapeCsv(employee.getEmployeeNumber()) + "," +
                            escapeCsv(employee.getName()) + "," +
                            escapeCsv(employee.getEmail()) + "," +
                            escapeCsv(String.valueOf(employee.getDepartment())) + "," +
                            escapeCsv(employee.getPosition()) + "," +
                            escapeCsv(employee.getHireDate() != null ? employee.getHireDate().toString() : "") + "," +
                            escapeCsv(String.valueOf(employee.getStatus()))
            );
            writer.newLine();
          }
        }
        writer.flush();

        // 파일을 Resource로 변환
        FileSystemResource resource = new FileSystemResource(csvFile);

        // ResponseEntity 생성
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=backup.csv")
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(resource);
      }
    } catch (IOException e) {
      if (csvFile != null && csvFile.exists()) {
        csvFile.delete();
      }
      throw new RestApiException(BackupErrorCode.BACKUP_FILE_CREATION_FAILED, e.getMessage());
    }
  }

  public MultipartFile convertFileToMultipartFile(File file) throws IOException {
    FileInputStream inputStream = new FileInputStream(file);
    return new MockMultipartFile(
            "file", // 파라미터 이름 (폼에서 사용하는 이름)
            file.getName(), // 파일 이름
            "text/csv", // CSV 파일 MIME 타입
            inputStream); // 파일의 InputStream
  }


  private MultipartFile logError(Exception e) {
    File logFile = new File("backup_error.log");

    try (FileWriter fileWriter = new FileWriter(logFile, true);
         BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

      bufferedWriter.write(LocalDateTime.now() + " : " + e.getMessage());
      bufferedWriter.newLine();
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    if (logFile.exists()) {
      try {
        // File을 MultipartFile로 변환하는 방법
        MultipartFile multipartFile = new MockMultipartFile(
                logFile.getName(),                // 파일 이름
                logFile.getName(),                // 원본 파일 이름
                "text/plain",                     // MIME 타입
                new FileInputStream(logFile)      // 파일 내용
        );

        // 파일 DB에 저장
        BigInteger fileSize = BigInteger.valueOf(logFile.length());
        com.sprint.example.sb01part2hrbankteam10.entity.File file = com.sprint.example.sb01part2hrbankteam10.entity.File.builder()
                .name(logFile.getName())
                .contentType("text/plain")
                .size(fileSize)
                .build();

        Integer fileId = fileRepository.save(file).getId();

        // 변환된 MultipartFile을 사용하여 저장
        fileStorage.saveBackup(fileId, multipartFile);
        return multipartFile;

      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    }
  return null;
  }


  private List<EmployeeForBackupDto> fetchEmployeeData() {

    List<EmployeeDto> employeeDtoList = employeeRepository.findAll()
            .stream()
            .map(EmployeeMapper::toDto)
            .toList();

    List<EmployeeForBackupDto> employeeForBackupDtoList = new ArrayList<>();
    for(EmployeeDto dto : employeeDtoList){
      Integer id = dto.getId();
      String name = dto.getName();
      String email = dto.getEmail();
      String employeeNumber = dto.getEmployeeNumber();
      Employee.EmployeeStatus status = dto.getStatus();
      String position = dto.getPosition();
      String departmentName = dto.getDepartmentName();
      LocalDate hireDate = dto.getHireDate();
      EmployeeForBackupDto employeeForBackupDto = EmployeeForBackupDto.builder()
              .id(id)
              .name(name)
              .email(email)
              .employeeNumber(employeeNumber)
              .status(status)
              .position(position)
              .departmentName(departmentName)
              .hireDate(hireDate)
              .build();
      employeeForBackupDtoList.add(employeeForBackupDto);
    }
    return employeeForBackupDtoList;
  }

  @Override
  public Page<BackupDto> getBackupList(
          String workerIpAddress,
          Backup.BackupStatus status,
          LocalDateTime startedAtFrom,
          LocalDateTime startedAtTo,
          Integer idAfter,
          String cursor,
          int size,
          String sortField,
          Sort.Direction sortDirection) {

    // cursor가 null인 경우 처리 (예: idAfter를 기본값으로 사용)
    if (cursor == null) {
      cursor = "";  // 적절한 기본값을 설정
    }

    // 커서가 주어진 경우 이를 idAfter로 변환
    if (cursor != null && !cursor.isEmpty()) {
      try {
        idAfter = Integer.valueOf(cursor);
      } catch (NumberFormatException e) {
        // 예외 처리: 잘못된 커서 형식
        throw new IllegalArgumentException("Invalid cursor format.");
      }
    }

    Pageable pageable = PageRequest.of(0, size, Sort.by(sortDirection, sortField));

    Page<BackupDto> backups = backupRepository.findBackups(workerIpAddress, status, startedAtFrom, startedAtTo, idAfter, pageable);

    return backups.map(backup -> modelMapper.map(backup, BackupDto.class));
  }

}