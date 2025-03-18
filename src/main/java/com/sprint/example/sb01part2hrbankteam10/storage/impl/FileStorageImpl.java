package com.sprint.example.sb01part2hrbankteam10.storage.impl;

import com.sprint.example.sb01part2hrbankteam10.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.FileErrorCode;
import com.sprint.example.sb01part2hrbankteam10.storage.FileStorage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileStorageImpl implements FileStorage {

  // 저장 디렉토리 경로 선언
  private final Path rootStorage;

  // 경로 의존성 주입 생성자
  public FileStorageImpl(@Value("${user.dir}/savedFiles") String rootStorage) {
    this.rootStorage= Path.of(rootStorage);
    init();
  }

  // 해당 디렉토리가 없을 시 만들어주는 초기화 메서드
  private void init(){
    try {
      Files.createDirectories(rootStorage);
    } catch(IOException e){
      throw new RestApiException(FileErrorCode.DIRECTORY_CREATION_FAILED, e.getMessage());
    }
  }

  // 프로필 파일 저장
  @Override
  public Integer saveProfile(Integer fileId, MultipartFile file){
    // fileId로 경로 생성 -> resolvePath
    Path path = resolvePath(fileId);

    // byte[] data를 로컬저장소에 넣기
    try{
      byte[] data = file.getBytes();
      Files.write(path, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }catch(IOException e){
      throw new RestApiException(FileErrorCode.FILE_WRITE_ERROR, e.getMessage());
    }
    return fileId;
  }

  // 백업 파일 저장
  @Override
  public Integer saveBackup(Integer fileId, MultipartFile file) {
    // fileId로 경로 생성
    Path path = resolvePath(fileId);

    // 파일을 스트림을 통해 저장
    try (InputStream inputStream = file.getInputStream();
        OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

      byte[] buffer = new byte[32768]; // 32KB씩 저장
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
    } catch (IOException e) {
      throw new RestApiException(FileErrorCode.FILE_STREAM_ERROR, e.getMessage());
    }

    return fileId;
  }


  // 아이디로 경로를 설정하는 메서드 resolvePath
  @Override
  public Path resolvePath(Integer fileId){
    return rootStorage.resolve(fileId.toString());
  }
}

