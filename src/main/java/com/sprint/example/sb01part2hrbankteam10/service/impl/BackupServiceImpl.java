package com.sprint.example.sb01part2hrbankteam10.service.impl;

import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10.entity.Backup;
import com.sprint.example.sb01part2hrbankteam10.entity.Employee;
import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.BackupErrorCode;
import com.sprint.example.sb01part2hrbankteam10.entity.Backup.BackupStatus;
import com.sprint.example.sb01part2hrbankteam10.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10.mapper.EmployeeMapper;
import com.sprint.example.sb01part2hrbankteam10.repository.BackupRepository;
import com.sprint.example.sb01part2hrbankteam10.repository.EmployeeHistoryRepository;
import com.sprint.example.sb01part2hrbankteam10.repository.EmployeeRepository;
import com.sprint.example.sb01part2hrbankteam10.repository.FileRepository;
import com.sprint.example.sb01part2hrbankteam10.service.BackupService;
import com.sprint.example.sb01part2hrbankteam10.storage.FileStorage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

@Service
@RequiredArgsConstructor
public class BackupServiceImpl implements BackupService {

  private final BackupRepository backupRepository;
  private final FileRepository fileRepository;
  private final FileStorage fileStorage;
  private final EmployeeRepository employeeRepository;
  private final EmployeeHistoryRepository employeeHistoryRepository;

  // 백업 메서드
  @Override
  public Integer performBackup(String workerIpAddress) {

    // 로직: if 백업 불필요 -> 건너뜀 상태로 배치이력 저장하고 프로세스 종료
    if (!isBackupNeeded()) {
      Backup backupHistory = createBackupHistory(workerIpAddress, BackupStatus.SKIPPED, null,
          null);
      return backupRepository.save(backupHistory).getId();
    }

    // 진행중 상태로 배치이력 저장, 작업자는 요청자의 ip 주소
    Backup atStartBackupHistory = createBackupHistory(workerIpAddress, BackupStatus.IN_PROGRESS,
        LocalDateTime.now(), null);
   backupRepository.save(atStartBackupHistory);

    MultipartFile backupFile = null;
    // 백업
    try {
      // 백업 로직 수행 (직원 정보 가져오기)
      HashMap<Integer, EmployeeDto> backupContent = fetchEmployeeData();

      // 전체 직원 정보를 CSV 파일로 저장 = 스트리밍/버퍼
      backupFile = convertBackupToCsvFile(backupContent);

      // 파일 저장
      Integer fileId = fileRepository.findByFileName(backupFile.getOriginalFilename());
      fileStorage.saveBackup(fileId, backupFile);

      // 백업 성공 -> 백업이력 완료로 수정
      Backup completedBackupHistory = createBackupHistory(workerIpAddress, BackupStatus.COMPLETED,
          atStartBackupHistory.getStartedAt(), LocalDateTime.now());
      return backupRepository.save(completedBackupHistory).getId();

    } catch (Exception e) {
      // 저장하던 파일 삭제, 에러로그 .log 파일로 저장, 백업이력 실패로 수정
      logError(e);

      Backup failedBackupHistory = createBackupHistory(workerIpAddress, BackupStatus.FAILED,
          atStartBackupHistory.getStartedAt(), null);
      backupRepository.save(failedBackupHistory);

      // 파일이 생성되었을 경우에만 삭제
      if (backupFile != null) {
        Integer fileId = fileRepository.findByFileName(backupFile.getOriginalFilename());
        if (fileId != null) {
          fileRepository.deleteById(fileId);
          fileStorage.deleteById(fileId);
        }
      }
      throw new RestApiException(BackupErrorCode.BACKUP_ERROR, failedBackupHistory.getId().toString());
    }
  }

  // 백업 여부 결정 로직: 가장 최근 완료된 배치 작업시간 < 직원 데이터 변경 시간 -> 백업 필요
  private boolean isBackupNeeded() {
    LocalDateTime lastBackupTime = backupRepository.findLastCompletedBackupAt();
    if (lastBackupTime == null) {
      lastBackupTime = LocalDateTime.MIN;
    }

    LocalDateTime lastEmployeeUpdate = employeeHistoryRepository.findLastModifiedAt();
    if (lastEmployeeUpdate == null) {
      lastEmployeeUpdate = LocalDateTime.MIN;
    }

    return lastBackupTime.isBefore(lastEmployeeUpdate);
  }

  private Backup createBackupHistory(String workerIpAddress, BackupStatus status, LocalDateTime startedAt, LocalDateTime endedAt) {
    return Backup.builder()
        .workerIpAddress(workerIpAddress)
        .status(status)
        .startedAt(startedAt)
        .endedAt(endedAt)
        .build();
  }

  private MultipartFile convertBackupToCsvFile(HashMap<Integer, EmployeeDto> backupContent) {
    File tempFile;
    try {
      tempFile = File.createTempFile("backup_", ".csv");
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
        writer.write("ID,Name,Position,UpdatedAt");
        writer.newLine();

        for (EmployeeDto employeeDto : backupContent.values()) {
          Employee employee = employeeRepository.findById(employeeDto.getId()).orElseThrow();
          writer.write(employee.getId() + "," + employee.getName() + "," + employee.getPosition() + "," + employee.getUpdatedAt());
          writer.newLine();
        }
      }
      return new MockMultipartFile("backup.csv", "backup.csv", "text/csv", new FileInputStream(tempFile));
    } catch (IOException e) {
      throw new RestApiException(BackupErrorCode.BACKUP_TO_CSV_FAILED, e.getMessage());
    }
  }

  private void logError(Exception e) {
    try (FileWriter fileWriter = new FileWriter("backup_error.log", true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
      bufferedWriter.write(LocalDateTime.now() + " : " + e.getMessage());
      bufferedWriter.newLine();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }


  private HashMap<Integer, EmployeeDto> fetchEmployeeData() {
    List<EmployeeDto> employeeDtoList = employeeRepository.findAll()
        .stream()
        .map(EmployeeMapper::toDto) // Employee → EmployeeDto 변환
        .toList(); // 리스트 변환

    return employeeDtoList.stream()
        .collect(Collectors.toMap(EmployeeDto::getId, Function.identity(), (a, b) -> b, HashMap::new));
  }

}