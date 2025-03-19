package com.sprint.example.sb01part2hrbankteam10.controller;

import com.sprint.example.sb01part2hrbankteam10.entity.File;
import com.sprint.example.sb01part2hrbankteam10.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.FileErrorCode;
import com.sprint.example.sb01part2hrbankteam10.repository.FileRepository;
import com.sprint.example.sb01part2hrbankteam10.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/files")
@RequiredArgsConstructor
public class FileController {

  private final FileStorage fileStorage;
  private final FileRepository fileRepository;

  @GetMapping("/{id}/download")
  public ResponseEntity<Resource> download(@PathVariable(value = "id") Integer fileId) {
    String contentType = fileRepository.findById(fileId)
        .map(File::getContentType)
        .orElseThrow(() -> new RestApiException(FileErrorCode.FILE_NOT_FOUND,"파일을 찾을 수 없습니다."));

    Resource resource = null;
    if ("text/csv".equals(contentType)){
      resource = fileStorage.downloadBackup(fileId);
    }else {
      resource = fileStorage.downloadProfile(fileId);
    }
    return ResponseEntity.status(HttpStatus.OK).body(resource);
  }
}
