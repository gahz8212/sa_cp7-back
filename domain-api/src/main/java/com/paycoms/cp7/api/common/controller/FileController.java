package com.paycoms.cp7.api.common.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.paycoms.cp7.api.common.dto.UploadFileRequest;
import com.paycoms.cp7.api.common.dto.UploadFileResponse;
import com.paycoms.cp7.api.common.service.FileService;
import com.paycoms.cp7.global.auth.annotation.LoginUser;
import com.paycoms.cp7.global.common.ApiResponse;
import com.paycoms.cp7.global.auth.common.UserInfoDto;
import com.paycoms.cp7.global.error.BusinessException;
import com.paycoms.cp7.global.util.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "File Controller", description = "File 관련 API")
@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
public class FileController {
  private final FileService fileService;
  private final MessageUtils messageUtils;

  @Operation(summary = "파일 업로드", description = "파일을 업로드합니다.")
  @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<List<UploadFileResponse>> uploadFile(@LoginUser UserInfoDto userInfo,
      @Valid @ModelAttribute UploadFileRequest request) {
    List<UploadFileResponse> response = new ArrayList<UploadFileResponse>();
    List<MultipartFile> files = request.getFiles();
    String uploadPath = request.getUploadPath();

    if (files == null || files.isEmpty()) {
      throw new BusinessException("FILE_001");
    }

    for (MultipartFile file : files) {
      try {
        response.add(fileService.uploadFile(userInfo.getId(), file, uploadPath));
      } catch (IOException e) {
        throw new BusinessException("COMM_001", e.getMessage());
      }
    }

    return messageUtils.createResponse("SYS_200", response);
  }
}
