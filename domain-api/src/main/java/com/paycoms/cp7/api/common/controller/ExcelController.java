package com.paycoms.cp7.api.common.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.paycoms.cp7.api.common.dto.DownloadExcelRequest;
import com.paycoms.cp7.api.common.dto.ModifiedRow;
import com.paycoms.cp7.api.common.dto.StructureExcelRequest;

import com.paycoms.cp7.api.common.dto.UpdateExcelRequest;
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
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.paycoms.cp7.global.auth.annotation.Auth;
import com.paycoms.cp7.global.auth.annotation.AuthPolicy;

@Slf4j
@Tag(name = "Excel Controller", description = "Excel 관련 API")
@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
public class ExcelController {
  private final ExcelService excelService;
  private final MessageUtils messageUtils;

  @Operation(summary = "엑셀 업로드", description = "엑셀 파일을 업로드합니다.")
  @Auth(AuthPolicy.PUBLIC) // 인증 없이 접근 허용
  @PostMapping(value = "/upload-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<UploadExcelResponse> uploadExcel(@LoginUser UserInfoDto userInfo,
      @Valid @ModelAttribute UploadExcelRequest request) {
    MultipartFile file = request.getFile();
    int sheetNo = request.getSheetNo();
    int rowNo = request.getRowNo();
    int currentPage = request.getPage();

    // 파일 확장자 및 Content-Type 체크 (.xlsx 및 .xls 허용)
    String contentType = file.getContentType();
    String originalFilename = file.getOriginalFilename();
    boolean isValidExcel = (contentType != null && (contentType.contains("spreadsheetml")
        || contentType.contains("vnd.ms-excel") || contentType.contains("application/octet-stream")))
        || (originalFilename != null && (originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls")));

    if (!isValidExcel) {
      throw new BusinessException("EXCEL_001");
    }

    UploadExcelResponse response = new UploadExcelResponse();

    // 인증 없이 요청을 보내면 @LoginUser UserInfoDto userInfo 파라미터가 null이 됩니다. 이 상태로 코드가 실행되면
    // userInfo.getId() 부분에서
    // NullPointerException이 발생합니다.
    // * 수정 내용: userInfo가 null일 경우를 대비해 기본값을 설정해 줍니다.
    // response.setUploadExcelKey(userInfo.getId() + "-" +
    // System.currentTimeMillis());
    String userId = (userInfo != null) ? userInfo.getId() : "anonymous";
    response.setUploadExcelKey(userId + "-" + System.currentTimeMillis());

    try {
      excelService.uploadExcel(response.getUploadExcelKey(), file, sheetNo, rowNo);

      // 업로드 직후 첫 페이지 데이터 조회
      response.setDataList(excelService.getExcelList(response.getUploadExcelKey(), currentPage, 1000));
      response.setTotalCount(excelService.getExcelCount(response.getUploadExcelKey()));

    } catch (IOException e) {
      throw new BusinessException("COMM_001", e.getMessage());
    }

    return messageUtils.createResponse("SYS_200", response);
  }
  // @Operation(summary = "엑셀 업데이트", description = "엑셀 파일을 업데이트합니다.")
  // @Auth(AuthPolicy.PUBLIC) // 인증 없이 접근 허용
  // @PostMapping(value = "/update-excel", consumes =
  // MediaType.MULTIPART_FORM_DATA_VALUE)
  // public ApiResponse<UploadExcelResponse> updateExcel(@LoginUser UserInfoDto
  // userInfo,
  // @Valid @ModelAttribute UploadExcelRequest request) {}

  @Operation(summary = "엑셀 수정 사항 저장", description = "그리드에서 수정된 엑셀 행 데이터를 반영합니다.")
  @Auth(AuthPolicy.PUBLIC) // 요청하신 대로 인증 제외 설정
  @PostMapping(value = "/save-excel-changes")
  public ApiResponse<String> saveExcelChanges(
      @LoginUser UserInfoDto userInfo,
      @Valid @RequestBody UpdateExcelRequest request) {

    List<ModifiedRow> changes = request.getModifiedRows();
    log.info(changes.toString());
    // 서비스 계층으로 전달하여 비즈니스 로직 처리
    excelService.updateModifiedRows(userInfo, changes);

    return new ApiResponse<>(200, "SYS_200", "총 " + changes.size() + "건의 수정 사항이 반영되었습니다.", "SUCCESS");
  }

  @Operation(summary = "엑셀 구조 저장", description = "엑셀 파일의 구조 데이터를 반영합니다.")
  @Auth(AuthPolicy.PUBLIC) // 요청하신 대로 인증 제외 설정
  @PostMapping(value = "/analyze-excel-structure")
  public ApiResponse<String> saveExcelStructure(
      @LoginUser UserInfoDto userInfo,
      @Valid @RequestBody StructureExcelRequest request) {

    List<List<String>> headers = request.getFlattenedHeaders();
    List<List<String>> dataSamples = request.getFlattenedData();
    List<List<String>> etcInfo = request.getFlattenedEtc();

    log.info(headers.toString());
    log.info(dataSamples.toString());
    log.info(etcInfo.toString());
    // 서비스 계층으로 전달하여 비즈니스 로직 처리
    // excelService.updateModifiedRows(userInfo, changes);

    return new ApiResponse<>(200, "SYS_200", "총 " + headers.size() + "건의 구조 데이터가 반영되었습니다.", "SUCCESS");
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
