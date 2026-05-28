package com.paycoms.cp7.api.common.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.paycoms.cp7.api.common.dto.DownloadExcelRequest;
import com.paycoms.cp7.api.common.dto.UploadExcelRequest;
import com.paycoms.cp7.api.common.dto.UploadExcelResponse;
import com.paycoms.cp7.api.common.service.ExcelService;
import com.paycoms.cp7.global.auth.annotation.LoginUser;
import com.paycoms.cp7.global.common.ApiResponse;
import com.paycoms.cp7.global.auth.common.UserInfoDto;
import com.paycoms.cp7.global.error.BusinessException;
import com.paycoms.cp7.global.util.MessageUtils;
import com.paycoms.cp7.global.util.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Excel Controller", description = "Excel 관련 API")
@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
public class ExcelController {
  private final ExcelService excelService;
  private final MessageUtils messageUtils;

  @Operation(summary = "엑셀 업로드", description = "엑셀 파일을 업로드합니다.")
  @PostMapping(value = "/upload-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<UploadExcelResponse> uploadExcel(@LoginUser UserInfoDto userInfo,
      @Valid @ModelAttribute UploadExcelRequest request) {
    MultipartFile file = request.getFile();
    int sheetNo = request.getSheetNo();
    int rowNo = request.getRowNo();

    // 파일 확장자 체크
    String contentType = file.getContentType();
    if (contentType == null || !contentType.contains("spreadsheetml")) {
      throw new BusinessException("EXCEL_001");
    }

    UploadExcelResponse response = new UploadExcelResponse();
    response.setUploadExcelKey(userInfo.getId() + "-" + System.currentTimeMillis());

    try {
      excelService.uploadExcel(response.getUploadExcelKey(), file, sheetNo, rowNo);
    } catch (IOException e) {
      throw new BusinessException("COMM_001", e.getMessage());
    }

    return messageUtils.createResponse("SYS_200", response);
  }

  @SuppressWarnings("null")
  @Operation(summary = "엑셀 다운로드", description = "엑셀 파일을 다운로드합니다.")
  @PostMapping(value = "/download-excel")
  public ResponseEntity<byte[]> downloadExcel(@Valid @RequestBody DownloadExcelRequest request) {
    if (request.getPasswordYn().equals("Y")) {
      ValidationUtils.validatePassword(request.getPassword());
    }

    try {
      byte[] excelContent = excelService.downloadExcel(request);

      // 파일명 한글 깨짐 방지 처리
      String fileName = URLEncoder.encode(request.getFileName(), StandardCharsets.UTF_8);

      if (request.getPasswordYn().equals("Y")) {
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(excelContent);
      } else {
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(excelContent);
      }
    } catch (IOException e) {
      throw new BusinessException("COMM_001", e.getMessage());
    }
  }
}
