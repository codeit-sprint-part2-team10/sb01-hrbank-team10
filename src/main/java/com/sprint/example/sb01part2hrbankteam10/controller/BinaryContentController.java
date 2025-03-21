package com.sprint.example.sb01part2hrbankteam10.controller;

import com.sprint.example.sb01part2hrbankteam10.controller.docs.BinaryContentDocs;
import com.sprint.example.sb01part2hrbankteam10.entity.BinaryContent;
import com.sprint.example.sb01part2hrbankteam10.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10.global.exception.errorcode.BinaryContentErrorCode;
import com.sprint.example.sb01part2hrbankteam10.repository.BinaryContentRepository;
import com.sprint.example.sb01part2hrbankteam10.storage.BinaryContentStorage;
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
public class BinaryContentController implements BinaryContentDocs {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;

  @GetMapping("/{id}/download")
  @Override
  public ResponseEntity<Resource> download(@PathVariable(value = "id") Integer binaryContentId) {
    String contentType = binaryContentRepository.findById(binaryContentId)
        .map(BinaryContent::getContentType)
        .orElseThrow(() -> new RestApiException(BinaryContentErrorCode
            .BINARY_CONTENT_NOT_FOUND,"파일을 찾을 수 없습니다."));

    Resource resource = null;
    if ("text/csv".equals(contentType)){
      resource = binaryContentStorage.downloadBackup(binaryContentId);
    }else {
      resource = binaryContentStorage.downloadProfile(binaryContentId);
    }
    return ResponseEntity.status(HttpStatus.OK).body(resource);
  }
}
