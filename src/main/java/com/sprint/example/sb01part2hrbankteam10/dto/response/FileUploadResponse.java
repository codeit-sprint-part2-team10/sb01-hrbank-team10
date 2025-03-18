package com.sprint.example.sb01part2hrbankteam10.dto.response;

import com.sprint.example.sb01part2hrbankteam10.entity.File;
import java.math.BigInteger;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadResponse {
  Integer id;
  String name;
  String contentType;
  BigInteger size;

  // 정적 팩토리 메서드
  public static FileUploadResponse from(File file) {
    FileUploadResponse response = new FileUploadResponse();
    response.setId(file.getId());
    response.setName(file.getName());
    response.setContentType(file.getContentType());
    response.setSize(file.getSize());
    return response;
  }
}