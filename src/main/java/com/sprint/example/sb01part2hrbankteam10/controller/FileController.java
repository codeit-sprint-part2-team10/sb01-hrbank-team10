package com.sprint.example.sb01part2hrbankteam10.controller;

import com.sprint.example.sb01part2hrbankteam10.dto.response.FileUploadResponse;
import com.sprint.example.sb01part2hrbankteam10.service.FileService;
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

  @GetMapping("/{id}/download")
  public ResponseEntity<Resource> download(@PathVariable(value = "id") Integer fileId) {
    Resource resource = fileStorage.download(fileId);
    return ResponseEntity.status(HttpStatus.OK).body(resource);
  }
}
