package com.sprint.example.sb01part2hrbankteam10.service.impl;

import com.sprint.example.sb01part2hrbankteam10.dto.BackupDto;
import com.sprint.example.sb01part2hrbankteam10.dto.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10.entity.Backup;
import com.sprint.example.sb01part2hrbankteam10.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10.repository.BackupRepository;
import com.sprint.example.sb01part2hrbankteam10.service.BackupService;
import java.time.LocalDateTime;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BackupServiceImpl implements BackupService {

  private final BackupRepository backupRepository;

  // 백업 메서드
  public Integer performBackup(String workerIpAddress) {

    // 로직: if 백업 불필요 -> 건너뜀 상태로 배치이력 저장하고 프로세스 종료
    if (!isBackupNeeded()) {
      BackupDto skippedBackupHistory = createBackupHistory(workerIpAddress, BackupType.SKIPPED, null,
          null);
      backupRepository.save(skippedBackupHistory);
      return 0;
    }

    // 진행중 상태로 배치이력 저장, 작업자는 요청자의 ip 주소
    BackupDto atStartBackupHistory = createBackupHistory(workerIpAddress, BackupType.IN_PROGRESS,
        LocalDateTime.now(), null);
    backupRepository.save(atStartBackupHistory);

    // 백업
    try {
      HashMap<Integer, EmployeeDto> backupContent = fetchEmployeeData();

      // 전체 직원 정보를 CSV 파일로 저장 = 스트리밍/버퍼
      MultipartFile backupFile = convertBackupToCsvFile(backupContent);

      fileRepository.save(backupFile);
      fileStorage.save(backupFile.getId(), backupFile); // TODO .getId()

    } catch(Exception e){
      // 저장하던 파일 삭제, 에러로그 .log 파일로 저장, 백업이력 실패로 수정

      BackupDto failedBackupHistory = createBackupHistory(workerIpAddress, BackupType.FAILED,
          atStartBackupHistory.startedAt, null);
      backupRepository.save(atFinalBackupHistory);

      fileRepository.delete(backupFile);
      fileStorage.delete(backupFile);

      throw new RestApiException(HttpStats.BACKUP_ERROR, logError(e));
    }



    // 백업 성공 -> 백업이력 완료로 수정
    BackupDto completedBackupHistory = createBackupHistory(workerIpAddress, BackupType.COMPLETED,
        atStartBackupHistory.startedAt, LocalDateTime.now());
    backupRepository.save(completedBackupHistory);

    return completedBackupHistory.getId();
  }

  // 백업 여부 결정 로직:private - 가장 최근 완료된 배치 작업시간 < 직원 데이터 변경 시간 -> 백입 필업 private 로직
  private boolean isBackupNeeded(){
    if(Backup backup = ; backup.endedAt().가장 최근<employee_history.modifiedAt()){
      return true;
    } else {
      return false;
    }

    private BackupDto createBackupHistory(String workerIpAddress, BackupType status, LocalDateTime startedAt, LocalDateTime endedAt){
      return BackupDto backupHistory = BackupDto.builder()
          .workerIpAddress(workerIpAddress)
          .status(status)
          .startedAt(startedAt)
          .endedAt(endedAt)
          .build();
    }

    private MultipartFile convertBackupToCsvFile(HashMap<EmployeeDto> backupContent){

    }

    private errorLog(){
      // 로그 파일로 저장
    }
}