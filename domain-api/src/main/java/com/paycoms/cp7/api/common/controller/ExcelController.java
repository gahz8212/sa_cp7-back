package com.paycoms.cp7.api.common.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.paycoms.cp7.api.common.dto.*;
import com.paycoms.cp7.api.common.service.ExcelService;
import com.paycoms.cp7.global.auth.annotation.LoginUser;
import com.paycoms.cp7.global.common.ApiResponse;
import com.paycoms.cp7.global.auth.common.UserInfoDto;
import com.paycoms.cp7.global.error.BusinessException;
import com.paycoms.cp7.global.util.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.paycoms.cp7.global.auth.annotation.Auth;
import com.paycoms.cp7.global.auth.annotation.AuthPolicy;

import org.springframework.web.bind.annotation.GetMapping;
import java.util.Arrays;
import java.util.ArrayList;
import com.paycoms.cp7.api.common.constant.ExcelTemplateType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paycoms.cp7.api.common.model.ExcelMappingTemplate;

@Slf4j
@Tag(name = "Excel Controller", description = "Excel 관련 API")
@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
public class ExcelController {
  private final ExcelService excelService;
  private final MessageUtils messageUtils;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Operation(summary = "엑셀 템플릿 타입 목록 조회", description = "시스템에 정의된 엑셀 템플릿 타입(WORKER, PARTS 등)과 메타데이터 목록을 조회합니다.")
  @Auth(AuthPolicy.PUBLIC)
  @GetMapping(value = "/excel-templates")
  public ApiResponse<List<ExcelTemplateType>> getExcelTemplates() {
      return messageUtils.createResponse("SYS_200", Arrays.asList(ExcelTemplateType.values()));
  }

  @Operation(summary = "엑셀 업로드", description = "엑셀 파일을 업로드합니다.")
  @Auth(AuthPolicy.PUBLIC)
  @PostMapping(value = "/upload-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<ExcelApiDto.UploadResponse> uploadExcel(@LoginUser UserInfoDto userInfo,
      @Valid @ModelAttribute ExcelApiDto.UploadRequest request) {
    MultipartFile file = request.getFile();
    int sheetNo = request.getSheetNo();
    int rowNo = request.getRowNo();
    int currentPage = request.getPage();

    String originalFilename = file.getOriginalFilename();
    ExcelApiDto.UploadResponse response = new ExcelApiDto.UploadResponse();

    // int lastDotIndex = originalFilename != null ? originalFilename.lastIndexOf(".") : 0;
    String userId = (userInfo != null) ? userInfo.getId() : (originalFilename != null ? originalFilename : "unknown");
    response.setUploadExcelKey(userId + "-" + System.currentTimeMillis());

    try {
      excelService.uploadExcel(response.getUploadExcelKey(), file, sheetNo, rowNo);
      response.setDataList(excelService.getExcelList(response.getUploadExcelKey(), currentPage, 1000));
      response.setTotalCount(excelService.getExcelCount(response.getUploadExcelKey()));

      // 통합 모델 적용: 파일명 기준으로 저장된 템플릿 정보를 반환
      response.setTargetColumns(excelService.getSysMetadata(originalFilename, userInfo));
      
      ExcelMappingTemplate savedTemplate = excelService.getSavedTemplate(originalFilename, userInfo);
      if (savedTemplate != null && savedTemplate.getHeaderStructure() != null) {
          try {
              Object headerStructureObj = objectMapper.readValue(savedTemplate.getHeaderStructure(), Object.class);
              response.setHeaderStructure(headerStructureObj);
          } catch (Exception e) {
              log.error("Failed to parse headerStructure", e);
          }
      }
      
      response.setTargetSysType("UNKNOWN"); // You can keep it as UNKNOWN or remove from response model later.
    } catch (IOException e) {
      throw new BusinessException("COMM_001", e.getMessage());
    }

    return messageUtils.createResponse("SYS_200", response);
  }


  @Operation(summary = "헤더,데이터,기타 구조", description = "엑셀 구조 데이터를 업로드합니다.")
  @Auth(AuthPolicy.PUBLIC)
  @PostMapping(value = "/analyze-excel-structure")
  public ApiResponse<ExcelApiDto.UploadResponse> analyzeExcelStructure(@LoginUser UserInfoDto userInfo,
      @Valid @RequestBody ExcelApiDto.StructureRequest request) {
        return messageUtils.createResponse("SYS_200", null);
      }



  @Operation(summary = "엑셀 데이터 및 템플릿 저장", description = "수정된 데이터와 매핑 템플릿을 함께 저장합니다.")
  @Auth(AuthPolicy.PUBLIC)
  @PostMapping(value = "/save-excel-data-and-template")
  public ApiResponse<String> saveExcelDataAndTemplate(
      @LoginUser UserInfoDto userInfo,
      @Valid @RequestBody ExcelApiDto.SaveDataAndTemplateRequest request) {

    excelService.saveExcelDataAndTemplate(userInfo, request);
    return new ApiResponse<>(200, "SYS_200", "데이터와 매핑 템플릿이 저장되었습니다.", "SUCCESS");
  }

  @Operation(summary = "엑셀 수정 사항 저장", description = "그리드에서 수정된 엑셀 행 데이터를 반영합니다.")
  @Auth(AuthPolicy.PUBLIC)
  @PostMapping(value = "/save-excel-changes")
  public ApiResponse<String> saveExcelChanges(
      @LoginUser UserInfoDto userInfo,
      @Valid @RequestBody ExcelApiDto.UpdateRequest request) {

    List<ExcelApiDto.ModifiedRow> changes = request.getModifiedRows();
    excelService.updateModifiedRows(userInfo, changes);
    return new ApiResponse<>(200, "SYS_200", "총 " + changes.size() + "건의 수정 사항이 반영되었습니다.", "SUCCESS");
  }
}