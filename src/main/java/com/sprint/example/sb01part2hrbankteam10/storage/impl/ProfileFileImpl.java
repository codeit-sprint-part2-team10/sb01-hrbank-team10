package com.sprint.example.sb01part2hrbankteam10.storage.impl;

import com.sprint.example.sb01part2hrbankteam10.mapper.FileMapper;
import com.sprint.example.sb01part2hrbankteam10.storage.FileStorage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component
public class ProfileFileImpl extends FileMapper implements FileStorage {

  // 경로 의존성 주입 생성자
  public ProfileFileImpl(@Value("${user.dir}/savedFiles/profiles") String rootStorage) {
    this.rootStorage = Path.of(rootStorage);
    init();
  }

  // 파일 저장
  @Override
  public Integer save(Integer fileId, MultipartFile file){
    // fileId로 경로 생성 -> resolvePath
    Path path = resolvePath(fileId);

    // file 데이터를 byte[]에 넣기
    byte[] data;
    try{
      data = file.getBytes();
    }catch(IOException e){
      throw new RuntimeException("파일 쓰기 중 오류가 발생했습니다." + e);
    }

    // byte[] data를 로컬저장소에 넣기
    try{
      Files.write(path, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }catch(IOException e){
      throw new RuntimeException("파일 저장 중 오류가 발생했습니다." + e);
    }
    return fileId;
  }
}
