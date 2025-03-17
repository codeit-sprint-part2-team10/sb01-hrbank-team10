package com.sprint.example.sb01part2hrbankteam10.storage;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface FileStorage {
  // 파일 저장
  Integer save(Integer fileId, MultipartFile file);

  // 파일 다운로드

}
