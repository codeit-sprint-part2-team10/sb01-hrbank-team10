package com.sprint.example.sb01part2hrbankteam10.storage;

import java.nio.file.Path;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface FileStorage {
  // 프로필 파일 저장
  Integer saveProfile(Integer fileId, MultipartFile file);

  // 백업 파일 저장
  Integer saveBackup(Integer fileId, MultipartFile file);

  // 파일 다운로드
  Resource download(Integer fileId);

  // 파일 경로 생성
  Path resolvePath(Integer fileId);
}
