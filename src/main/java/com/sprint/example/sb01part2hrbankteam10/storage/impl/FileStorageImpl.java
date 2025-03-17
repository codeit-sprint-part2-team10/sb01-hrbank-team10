package com.sprint.example.sb01part2hrbankteam10.storage.impl;

import com.sprint.example.sb01part2hrbankteam10.storage.FileStorage;
import java.io.IOException;
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
  private final Path profileStorage;
  private final Path backupStorage;

  // 경로 의존성 주입 생성자
  public FileStorageImpl(@Value("${user.dir}/savedFiles/profiles") String profileStorage, @Value("${user.dir}/savedFiles/backups")String backupStorage) {
    this.profileStorage= Path.of(profileStorage);
    this.backupStorage = Path.of(backupStorage);
    init();
  }

  // 해당 디렉토리가 없을 시 만들어주는 초기화 메서드
  private void init(){
    try {
      Files.createDirectories(profileStorage);
      Files.createDirectories(backupStorage);
    } catch(IOException e){
      throw new RuntimeException(e);
    }
  }

  // 프로필 파일 저장
  @Override
  public Integer saveProfile(Integer fileId, MultipartFile file){
    // fileId로 경로 생성 -> resolvePath
    Path path = profileResolvePath(fileId);

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

  // 백업 파일 저장
  @Override
  public Integer saveBackup(Integer fileId, MultipartFile file){
    // fileId로 경로 생성 -> resolvePath
    Path path = backupResolvePath(fileId);

    // file 데이터 output stream 생성
    byte[] data;
    try(OutputStream outputStream = Files.newOutputStream(path)){
      data = file.getBytes();
      outputStream.write(data);
    }catch(IOException e){
      throw new RuntimeException("백업 데이터 스트리밍 중 오류가 발생했습니다." + e);
    }

    // byte[] data를 로컬저장소에 넣기
    try{
      Files.write(path, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }catch(IOException e){
      throw new RuntimeException("파일 저장 중 오류가 발생했습니다." + e);
    }
    return fileId;
  }

  // 프로필 - 아이디로 경로를 설정하는 메서드 resolvePath
  @Override
  public Path profileResolvePath(Integer fileId){
    return profileStorage.resolve(fileId.toString());
  }

  // 백업 - 아이디로 경로를 설정하는 메서드 resolvePath
  @Override
  public Path backupResolvePath(Integer fileId){
    return backupStorage.resolve(fileId.toString());
  }
}

