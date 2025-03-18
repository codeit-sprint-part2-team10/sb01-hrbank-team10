package com.sprint.example.sb01part2hrbankteam10.service;

import org.springframework.stereotype.Service;
import com.sprint.example.sb01part2hrbankteam10.dto.response.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileService {
  FileUploadResponse uploadProfile(MultipartFile file);
  FileUploadResponse uploadBackup(MultipartFile file);
}