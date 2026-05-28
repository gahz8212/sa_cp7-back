package com.paycoms.cp7.api.common.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.paycoms.cp7.api.common.dto.UploadFileResponse;
import com.paycoms.cp7.global.error.BusinessException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {

  @Value("${file.upload-dir}")
  private String uploadDir;

  public UploadFileResponse uploadFile(String email, MultipartFile file, String uploadPath) throws IOException {
    if (file == null || file.isEmpty()) {
      throw new BusinessException("FILE_001");
    }

    // 저장 디렉토리 생성
    Path copyLocation = Paths.get(uploadDir + (uploadPath == null ? "" : uploadPath)).toAbsolutePath().normalize();
    if (!Files.exists(copyLocation)) {
      Files.createDirectories(copyLocation);
    }

    // 고유한 파일명 생성 (UUID + 확장자)
    String originalFilename = file.getOriginalFilename();
    String extension = "";
    if (originalFilename != null && originalFilename.contains(".")) {
      extension = originalFilename.substring(originalFilename.lastIndexOf("."));
    }
    String serverFileName = System.currentTimeMillis() + "-" + UUID.randomUUID().toString() + extension;

    // 파일 복사 (기존 파일이 있으면 덮어쓰기)
    Path targetPath = copyLocation.resolve(serverFileName);
    Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

    UploadFileResponse uploadFileResponse = new UploadFileResponse();

    uploadFileResponse.setOrgFileName(originalFilename);
    uploadFileResponse.setSaveFileName(serverFileName);

    return uploadFileResponse;
  }
}
