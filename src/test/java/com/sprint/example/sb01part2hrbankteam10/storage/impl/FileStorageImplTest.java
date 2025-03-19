package com.sprint.example.sb01part2hrbankteam10.storage.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.sprint.example.sb01part2hrbankteam10.storage.FileStorage;
import jakarta.transaction.Transactional;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Transactional
class FileStorageImplTest {

  @Autowired
  private FileStorage fileStorage;

  @Test
  void testSaveFileToLocalStorage(){

    // given
    Integer fileId = 12345; // 테스트용 파일 ID

    // MultipartFile 목 객체 생성
    String fileName = "user1_profile.png";
    String contentType = "testfile/zip";
    byte[] content = "테스트 이미지 내용".getBytes();

    MultipartFile testFile = new MockMultipartFile(
        "file",
        fileName,
        contentType,
        content
    );

    // when
    Integer savedProfileId = fileStorage.saveProfile(fileId, testFile);
    Integer savedBackupId = fileStorage.saveBackup(fileId, testFile);

    // then
    // 프로필 파일이 savedFiles/profiles에 저장되었나 확인
    Path profilePath = fileStorage.profileResolvePath(savedProfileId);
    File savedProfileFile = new File("profilePath");

    // 백업 파일이 savedFiles/backups에 저장되었나 확인
    Path backupPath = fileStorage.backupResolvePath(savedBackupId);
    File savedBackupFile = new File("backupPath");
  }

  @Test
  void testDeleteFileFromLocalStorage(){

    // given
    Integer fileId = 12345; // 테스트용 파일 ID

    // MultipartFile 목 객체 생성
    String fileName = "user1_profile.png";
    String contentType = "testfile/zip";
    byte[] content = "테스트 이미지 내용".getBytes();

    MultipartFile testFile = new MockMultipartFile(
        "file",
        fileName,
        contentType,
        content
    );

    // when
    fileStorage.saveProfile(fileId, testFile);
    fileStorage.saveBackup(fileId, testFile);
    fileStorage.deleteProfile(fileId);
    fileStorage.deleteBackup(fileId);

    // then
    Path profilePath = fileStorage.profileResolvePath(fileId);
    Path backupPath = fileStorage.backupResolvePath(fileId);

    // profilePath와 backupPath 경로가 존재하는지 확인
    assertFalse(Files.exists(Paths.get(profilePath.toUri())), "Profile file should be deleted");
    assertFalse(Files.exists(Paths.get(backupPath.toUri())), "Backup file should be deleted");

  }

}