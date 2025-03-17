package com.sprint.example.sb01part2hrbankteam10.storage.impl;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest // @DataJpaTest 대신 전체 애플리케이션 컨텍스트를 로드
@Transactional // 테스트 후 자동으로 롤백되도록 설정
class ProfileFileImplTest {

  @Autowired
  ProfileFileImpl profileFile; // FileStorage, FileMapper 대신 직접 구현체 주입

  @Test
  void saveProfileToLocalStorage() throws IOException {
    // given
    Integer fileId = 12345; // 테스트용 파일 ID

    // MultipartFile 목 객체 생성
    String fileName = "user1_profile.png";
    String contentType = "image/png";
    byte[] content = "테스트 이미지 내용".getBytes();

    MultipartFile profile = new MockMultipartFile(
        "file",
        fileName,
        contentType,
        content
    );

    // when
    profileFile.save(fileId, profile);

    // then
    Path path = profileFile.resolvePath(fileId);
    java.io.File savedFile = path.toFile();
    assertTrue(savedFile.exists(), "파일이 로컬 저장소에 저장되지 않았습니다.");

    // 테스트 후 파일 정리
    savedFile.delete();
  }
}