package com.sprint.example.sb01part2hrbankteam10.service.impl;

import com.sprint.example.sb01part2hrbankteam10.entity.File;
import com.sprint.example.sb01part2hrbankteam10.repository.FileRepository;
import com.sprint.example.sb01part2hrbankteam10.service.FileService;
import com.sprint.example.sb01part2hrbankteam10.storage.FileStorage;
import com.sprint.example.sb01part2hrbankteam10.dto.response.FileUploadResponse;
import jakarta.transaction.Transactional;
import java.math.BigInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

  private final FileStorage fileStorage;
  private final FileRepository fileRepository;

  // 프로필 파일 업로드
  @Override
  @Transactional
  public FileUploadResponse uploadProfile(MultipartFile multipartFile) {
    // 메타 정보 저장
    File file = File.builder()
        .name(multipartFile.getOriginalFilename())
        .contentType(multipartFile.getContentType())
        .size(BigInteger.valueOf(multipartFile.getSize()))
        .build();

    File savedFile = fileRepository.save(file);

    // 실제 파일을 로컬 저장소에 저장
    fileStorage.saveProfile(savedFile.getId(), multipartFile);

    return FileUploadResponse.from(savedFile);
  }

  // 백업 파일 업로드
  @Override
  @Transactional
  public FileUploadResponse uploadBackup(MultipartFile multipartFile) {
    // 메타 정보 저장
    File file = File.builder()
        .name(multipartFile.getOriginalFilename())
        .contentType(multipartFile.getContentType())
        .size(BigInteger.valueOf(multipartFile.getSize()))
        .build();

    File savedFile = fileRepository.save(file);

    // 실제 파일을 로컬 저장소에 저장
    fileStorage.saveBackup(savedFile.getId(), multipartFile);

    return FileUploadResponse.from(savedFile);
  }
}