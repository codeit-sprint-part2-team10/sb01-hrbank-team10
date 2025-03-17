package com.sprint.example.sb01part2hrbankteam10.mapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class FileMapper {

  // 저장 디렉토리 경로 선언
  public Path rootStorage;

  // 해당 디렉토리가 없을 시 만들어주는 초기화 메서드
  protected void init(){
    try {
      Files.createDirectories(rootStorage);
    } catch(IOException e){
      throw new RuntimeException(e);
    }
  }

  // 아이디로 경로를 설정하는 메서드 resolvePath
  // TODO : 그냥 다시 각각의 구현체로 보내는 쪽으로 수정 가능성 있음
  public Path resolvePath(Integer fileId){
    return rootStorage.resolve(fileId.toString());
  }
}
