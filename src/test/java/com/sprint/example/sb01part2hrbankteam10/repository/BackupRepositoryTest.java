package com.sprint.example.sb01part2hrbankteam10.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.sprint.example.sb01part2hrbankteam10.entity.Backup;
import com.sprint.example.sb01part2hrbankteam10.entity.Backup.BackupStatus;
import com.sprint.example.sb01part2hrbankteam10.entity.File;
import jakarta.transaction.Transactional;
import java.math.BigInteger;
import java.net.InetAddress;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 지금 설정된 실제 데이터 베이스를 사용하도록 설정
@Transactional // 테스트 후 자동으로 롤백되도록 설정
class BackupRepositoryTest {

  @Autowired
  private BackupRepository backupRepository;
  @Autowired
  private FileRepository fileRepository;

  @Test
  void testSaveBackup() {
    // given
    // 파일 생성
    File file = File.builder()
        .name("backup1")
        .contentType("application/zip")
        .size(new BigInteger("1024"))
        .build();
    fileRepository.save(file);

    // 백업 파일 생성 : 파일이 널이 아닐 때
    Backup backupFileNotNull = Backup.builder()
        .file(file)
        .startedAt(LocalDateTime.now())
        .endedAt(LocalDateTime.now())
        .workerIpAddress(InetAddress.getLoopbackAddress().getHostAddress())
        .status(BackupStatus.COMPLETED)
        .batchDoneAt(LocalDateTime.now())
        .build();
    backupRepository.save(backupFileNotNull);

    // 백업 생성 : 파일이 널일 때
    Backup backupFileIsNull = Backup.builder()
        .file(null)
        .startedAt(LocalDateTime.now())
        .endedAt(LocalDateTime.now())
        .workerIpAddress(InetAddress.getLoopbackAddress().getHostAddress())
        .status(BackupStatus.FAILED)
        .batchDoneAt(LocalDateTime.now())
        .build();
    backupRepository.save(backupFileIsNull);

    // when
    // 파일이 널이 아닐 때
    Backup resultCreateFileNotNull = backupRepository.findById(backupFileNotNull.getId()).orElseThrow();
    // 파일이 널일 때
    Backup resultCreateFileIsNull = backupRepository.findById(backupFileIsNull.getId()).orElseThrow();

    // then
    // 파일이 널이 아닐 때
    assertEquals(backupFileNotNull.getFile(), resultCreateFileNotNull.getFile());
    assertEquals(backupFileNotNull.getStartedAt(), resultCreateFileNotNull.getStartedAt());
    assertEquals(backupFileNotNull.getEndedAt(), resultCreateFileNotNull.getEndedAt());
    assertEquals(backupFileNotNull.getWorkerIpAddress(), resultCreateFileNotNull.getWorkerIpAddress());
    assertEquals(backupFileNotNull.getStatus(), resultCreateFileNotNull.getStatus());
    assertEquals(backupFileNotNull.getBatchDoneAt(), resultCreateFileNotNull.getBatchDoneAt());

    // 파일이 널일 때
    assertEquals(backupFileIsNull.getFile(), resultCreateFileIsNull.getFile());
    assertEquals(backupFileIsNull.getStartedAt(), resultCreateFileIsNull.getStartedAt());
    assertEquals(backupFileIsNull.getEndedAt(), resultCreateFileIsNull.getEndedAt());
    assertEquals(backupFileIsNull.getWorkerIpAddress(), resultCreateFileIsNull.getWorkerIpAddress());
    assertEquals(backupFileIsNull.getStatus(), resultCreateFileIsNull.getStatus());
    assertEquals(backupFileIsNull.getBatchDoneAt(), resultCreateFileIsNull.getBatchDoneAt());
  }

  @Test
  void testDeleteBackup() {
    // given
    // 파일 생성
    File file = File.builder()
        .name("backup1")
        .contentType("application/zip")
        .size(new BigInteger("1024"))
        .build();
    fileRepository.save(file);

    Backup backup = Backup.builder()
        .file(file)
        .startedAt(LocalDateTime.now())
        .endedAt(LocalDateTime.now())
        .workerIpAddress(InetAddress.getLoopbackAddress().getHostAddress())
        .status(BackupStatus.COMPLETED)
        .batchDoneAt(LocalDateTime.now())
        .build();
    backupRepository.save(backup);

    // when
    backupRepository.delete(backup);

    // then
    // 백업 삭제
    assertNull(backupRepository.findById(backup.getId()).orElse(null));
    // 파일도 같이 삭제 됐는지 확인 (참조관계)
    assertNull(fileRepository.findById(file.getId()).orElse(null));
  }
}