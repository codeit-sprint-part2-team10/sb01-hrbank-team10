//package com.sprint.example.sb01part2hrbankteam10.service.impl;
//
//import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.BackupErrorCode;
//import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDto;
//import com.sprint.example.sb01part2hrbankteam10.entity.Backup;
//import com.sprint.example.sb01part2hrbankteam10.entity.Backup.BackupStatus;
//import com.sprint.example.sb01part2hrbankteam10.global.exception.RestApiException;
//import com.sprint.example.sb01part2hrbankteam10.repository.BackupRepository;
//import com.sprint.example.sb01part2hrbankteam10.repository.EmployeeHistoryRepository;
//import com.sprint.example.sb01part2hrbankteam10.repository.EmployeeRepository;
//import com.sprint.example.sb01part2hrbankteam10.repository.FileRepository;
//import com.sprint.example.sb01part2hrbankteam10.service.BackupService;
//import com.sprint.example.sb01part2hrbankteam10.storage.FileStorage;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//@Service
//@RequiredArgsConstructor
//public class BackupServiceImpl implements BackupService {
//
//  private final BackupRepository backupRepository;
//  private final FileRepository fileRepository;
//  private final FileStorage fileStorage;
//  private final EmployeeRepository employeeRepository;
//  private final EmployeeHistoryRepository employeeHistoryRepository;
//
//  // 백업 메서드
//  @Override
//  public Integer performBackup(String workerIpAddress) {
//
//    // 로직: if 백업 불필요 -> 건너뜀 상태로 배치이력 저장하고 프로세스 종료
//    if (!isBackupNeeded()) {
//      Backup backupHistory = createBackupHistory(workerIpAddress, BackupStatus.SKIPPED, null,
//          null);
//      return backupRepository.save(backupHistory).getId();
//    }
//
//    // 진행중 상태로 배치이력 저장, 작업자는 요청자의 ip 주소
//    Backup atStartBackupHistory = createBackupHistory(workerIpAddress, BackupStatus.IN_PROGRESS, LocalDateTime.now(), null);
//    backupRepository.save(atStartBackupHistory);
//
//    // 백업
//    MultipartFile backupFile = null;
//    try {
//      // 백업 로직 수행 (직원 정보 가져오기)
//      HashMap<Integer, EmployeeDto> backupContent = fetchEmployeeData();
//
//      // 전체 직원 정보를 CSV 파일로 저장 = 스트리밍/버퍼
//      backupFile = convertBackupToCsvFile(backupContent);
//
//      Integer fileId = fileRepository.findByName(backupFile.getOriginalFilename());
//      fileStorage.saveBackup(fileId, backupFile);
//
//      // 백업 성공 -> 백업이력 완료로 수정
//      Backup completedBackupHistory = createBackupHistory(workerIpAddress, BackupStatus.COMPLETED, atStartBackupHistory.getStartedAt(), LocalDateTime.now());
//      return backupRepository.save(completedBackupHistory).getId();
//
//    } catch (Exception e) {
//      // 저장하던 파일 삭제, 에러로그 .log 파일로 저장, 백업이력 실패로 수정
//      logError(e);
//
//      Backup failedBackupHistory = createBackupHistory(workerIpAddress, BackupStatus.FAILED,
//          atStartBackupHistory.getStartedAt(), null);
//      backupRepository.save(failedBackupHistory);
//
//      if(backupFile != null) {
//        fileRepository.delete(backupFile);
//        fileStorage.delete(backupFile);
//      }
//      throw new RestApiException(BackupErrorCode.BACKUP_ERROR, e.getMessage());
//    }
//  }
//
//  // 백업 여부 결정 로직:private - 가장 최근 완료된 배치 작업시간 < 직원 데이터 변경 시간 -> 백입 필업 private 로직
//  private boolean isBackupNeeded() {
//    LocalDateTime lastBackupAt = backupRepository.findLastCompletedBackup();
//    if(lastBackupAt == null) {lastBackupAt = LocalDateTime.MIN;}
//
//    LocalDateTime lastEmployeeUpdateAt = employeeHistoryRepository.findLastModifiedAt();
//    if(lastEmployeeUpdateAt == null) {lastBackupAt = LocalDateTime.MIN;}
//
//    return lastBackupAt.isBefore(lastEmployeeUpdateAt);
//  }
//
//  private Backup createBackupHistory (String workerIpAddress, BackupStatus status, LocalDateTime
//  startedAt, LocalDateTime endedAt){
//    return Backup.builder()
//        .workerIpAddress(workerIpAddress)
//        .status(status)
//        .startedAt(startedAt)
//        .endedAt(endedAt)
//        .build();
//  }
//
//  private MultipartFile convertBackupToCsvFile (HashMap<Integer, EmployeeDto> backupContent) {
//    File tempFile;
//    try {
//      tempFile = File.createTempFile("backup_", ".csv");
//      try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
//        writer.write("ID,Name,Position,UpdatedAt");
//        writer.newLine();
//
//        for (EmployeeDto employee : backupContent.values()) {
//          writer.write(employee.getId() + "," + employee.getName() + "," + employee.getPosition() + "," + employee.getUpdatedAt());
//          writer.newLine();
//        }
//      }
//      return new MockMultipartFile("backup.csv", new FileInputStream(tempFile));
//    } catch (IOException e) {
//      throw new RestApiException(BackupErrorCode.BACKUP_TO_CSV_FAILED, "CSV 파일 생성 실패");
//    }
//  }
//
//  private void logError(e) {
//    try (FileWriter fileWriter = new FileWriter("backup_error.log", true);
//        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
//      bufferedWriter.write(LocalDateTime.now() + " : " + e.getMessage());
//      bufferedWriter.newLine();
//    } catch (IOException ex) {
//      ex.printStackTrace();
//    }
//
//  }
//
//  private HashMap<Long, EmployeeDto> fetchEmployeeData() {
//    List<EmployeeDto> employees = employeeRepository.findAll();
//    return employees.stream().collect(
//        Collectors.toMap(EmployeeDto::getId, Function.identity()));
//  }
//}