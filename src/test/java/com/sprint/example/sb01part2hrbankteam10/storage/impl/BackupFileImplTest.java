package com.sprint.example.sb01part2hrbankteam10.storage.impl;

import com.sprint.example.sb01part2hrbankteam10.storage.FileStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class BackupFileImplTest {

  @Autowired
  @Qualifier("backupFileImpl") // 특정 빈 이름으로 주입
  private FileStorage fileStorage;

  private Integer testFileId;

  @Test
  void saveBackupToLocalStorage() throws IOException {
    // given
    testFileId = 12345; // 테스트용 파일 ID

    // MultipartFile 목 객체 생성
    String fileName = "backup_test.txt";
    String contentType = "text/plain";
    byte[] content = "백업 테스트 데이터".getBytes();

    MultipartFile multipartFile = new MockMultipartFile(
        "file",
        fileName,
        contentType,
        content
    );

    // when
    fileStorage.save(testFileId, multipartFile);

    // then
    Path path = ((BackupFileImpl)fileStorage).resolvePath(testFileId);
    java.io.File savedFile = path.toFile();

    // 파일 존재 여부 확인
    assertTrue(savedFile.exists(), "파일이 로컬 저장소에 저장되지 않았습니다.");

    // 파일 내용 확인
    byte[] savedContent = Files.readAllBytes(path);
    assertArrayEquals(content, savedContent, "저장된 파일의 데이터가 원본과 일치하지 않습니다.");
  }


  @AfterEach
  void cleanup() throws IOException {
    // 테스트 후 생성된 파일 삭제
    if (testFileId != null) {
      Path path = ((BackupFileImpl)fileStorage).resolvePath(testFileId);
      Files.deleteIfExists(path);
    }
  }
}