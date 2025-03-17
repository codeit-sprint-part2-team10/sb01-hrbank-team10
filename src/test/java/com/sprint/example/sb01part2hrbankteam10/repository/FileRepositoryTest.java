package com.sprint.example.sb01part2hrbankteam10.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.sprint.example.sb01part2hrbankteam10.entity.File;
import jakarta.transaction.Transactional;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 지금 설정된 실제 데이터 베이스를 사용하도록 설정
@Transactional // 테스트 후 자동으로 롤백되도록 설정
class FileRepositoryTest {

  @Autowired
  private FileRepository fileRepository;

  @Test // 생성 테스트
  void testSaveFile() {

    // given
    File file = File.builder()
        .name("backup1")
        .contentType("application/zip")
        .size(new BigInteger("1024"))
        .build();
    fileRepository.save(file);

    // when
    File resultCreate = fileRepository.findById(file.getId()).orElse(null);

    // then
    assertNotNull(resultCreate);
    assertEquals(file.getName(), resultCreate.getName());
    assertEquals(file.getContentType(), resultCreate.getContentType());
    assertEquals(file.getSize(), resultCreate.getSize());
  }

  @Test // 삭제 테스트
  void testDeleteFile() {

    // given
    File file = File.builder()
        .name("backup1")
        .contentType("application/zip")
        .size(new BigInteger("1024"))
        .build();
    fileRepository.save(file);

    // when
    fileRepository.deleteById(file.getId());
    File getFile = fileRepository.findById(file.getId()).orElse(null);

    // then
    assertNull(getFile);
  }
}