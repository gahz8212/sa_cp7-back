package com.paycoms.cp7.api.common.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paycoms.cp7.api.common.constant.ExcelTemplateType;
import com.paycoms.cp7.api.common.dto.*;
import com.paycoms.cp7.api.common.mapper.ExcelMapper;
import com.paycoms.cp7.api.common.mapper.ExcelTemplateMapper;
import com.paycoms.cp7.api.common.model.Excel;
import com.paycoms.cp7.api.common.model.ExcelMappingTemplate;
import com.paycoms.cp7.global.auth.common.UserInfoDto;
import com.paycoms.cp7.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelService {

  private final SqlSessionFactory sqlSessionFactory;
  private final ExcelTemplateMapper excelTemplateMapper;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final DataFormatter dataFormatter = new DataFormatter();

  public void uploadExcel(String fileKey, MultipartFile file, int sheetNo) throws IOException {
    Workbook workbook = WorkbookFactory.create(file.getInputStream());
    Sheet sheet = workbook.getSheetAt(sheetNo);

    try {
      List<Excel> dataList = new ArrayList<>();
      List<Excel> emptyBuffer = new ArrayList<>();
      int bestRowNo = findBestHeaderRow(sheet);

      Row bestRow = sheet.getRow(bestRowNo);
      List<String> headers = new ArrayList<>();
      if (bestRow != null) {
        for (int j = 0; j < bestRow.getLastCellNum(); j++) {
          Cell cell = bestRow.getCell(j);
          headers.add(getCellValueAsString(cell));
        }
      }

      for (int i = 0; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        List<String> rowData = new ArrayList<>();
        boolean isAllEmpty = true;
        for (int j = 0; j < headers.size(); j++) {
          Cell cell = (row != null) ? row.getCell(j) : null;
          String val = (cell != null ? getCellValueAsString(cell) : "");
          rowData.add(val);
          if (val != null && !val.trim().isEmpty()) {
            isAllEmpty = false;
          }
        }

        Excel excelObj = new Excel();
        excelObj.setFileKey(fileKey);
        excelObj.setRowIndex(i);
        excelObj.setDataJson(rowData);

        if (isAllEmpty) {
          emptyBuffer.add(excelObj);
          if (emptyBuffer.size() >= 10) {
            // 10개 이상의 연속된 빈 행 발견 시, 첫 번째 빈 행만 추가하고 중단
            // dataList.add(emptyBuffer.get(0));
            break;
          }
        } else {
          // 데이터가 있는 행을 만나면 대기 중인 빈 행들을 모두 추가
          dataList.addAll(emptyBuffer);
          emptyBuffer.clear();
          dataList.add(excelObj);
        }

        if (dataList.size() >= 1000) {
          saveExcels(new ArrayList<>(dataList));
          dataList.clear();
        }
      }

      // 루프 종료 후 남은 데이터 저장 (10개 미만의 빈 행으로 끝난 경우 등)
      if (!dataList.isEmpty()) {
        saveExcels(new ArrayList<>(dataList));
      }
    } finally {
      workbook.close();
    }
  }

  private int findBestHeaderRow(Sheet sheet) {
    int maxCells = -1;
    int bestRowIndex = 0;
    int scanLimit = Math.min(sheet.getLastRowNum(), 30);
    for (int i = 0; i <= scanLimit; i++) {
      Row row = sheet.getRow(i);
      if (row == null)
        continue;
      int currentCells = 0;
      for (int j = 0; j < row.getLastCellNum(); j++) {
        Cell cell = row.getCell(j);
        if (cell != null && !getCellValueAsString(cell).trim().isEmpty())
          currentCells++;
      }
      if (currentCells > maxCells) {
        maxCells = currentCells;
        bestRowIndex = i;
      }
    }
    return bestRowIndex;
  }

  private String getCellValueAsString(Cell cell) {
    if (cell == null)
      return "";
    return dataFormatter.formatCellValue(cell);
  }

  public void updateModifiedRows(UserInfoDto userInfo, List<ExcelApiDto.ModifiedRow> changes) {
    for (ExcelApiDto.ModifiedRow row : changes) {
      log.info("행 {}번 업데이트 수행: {}", row.getRowIndex(), row.getModified());
    }
  }

  @Transactional
  public void saveExcelDataAndTemplate(UserInfoDto userInfo, ExcelApiDto.SaveDataAndTemplateRequest request) {
    if (request.getModifiedRows() != null) {
      updateModifiedRows(userInfo, request.getModifiedRows());
    }

    if (request.getTemplateData() != null) {
      ExcelApiDto.SaveDataAndTemplateRequest.TemplateDto templateDto = request.getTemplateData();
      String userId = userInfo != null ? userInfo.getId() : "anonymous";
      String fileName = templateDto.getFileName();

      ExcelMappingTemplate existing = excelTemplateMapper.selectTemplateByNameAndUser(fileName, userId);

      ExcelMappingTemplate template = new ExcelMappingTemplate();
      template.setTemplateName(fileName);
      // template.setTargetSysType("UNKNOWN"); // Or remove this field entirely if DB
      // allows
      template.setUserId(userId);

      try {
        template.setStructures(objectMapper.writeValueAsString(templateDto.getStructures()));
        template.setMappingRules(objectMapper.writeValueAsString(templateDto.getTargetColumns()));
      } catch (Exception e) {
        throw new BusinessException("COMM_001", "JSON 변환 오류");
      }
      log.info("Saving template for user {}: {}", userId, template);
      if (existing != null) {
        excelTemplateMapper.updateTemplate(template);
      } else {
        excelTemplateMapper.insertTemplate(template);
      }
    }
  }

  @Transactional(readOnly = true)
  public List<Excel> getExcelList(String fileKey, int page, int size) {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      ExcelMapper mapper = sqlSession.getMapper(ExcelMapper.class);
      return mapper.selectExcelList(fileKey, size, (page - 1) * size);
    }
  }

  @Transactional(readOnly = true)
  public int getExcelCount(String fileKey) {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      ExcelMapper mapper = sqlSession.getMapper(ExcelMapper.class);
      return mapper.selectExcelCount(fileKey);
    }
  }

  @Transactional
  public void saveExcels(List<Excel> excelList) {
    try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
      ExcelMapper mapper = sqlSession.getMapper(ExcelMapper.class);
      for (Excel excel : excelList) {
        mapper.insertExcel(excel);
      }
      sqlSession.flushStatements();
      sqlSession.commit();
    } catch (Exception e) {
      throw new BusinessException("EXCEL_003", e.getMessage());
    }
  }

  public ExcelMappingTemplate getSavedTemplate(String fileName, UserInfoDto userInfo) {
    String userId = userInfo != null ? userInfo.getId() : "anonymous";
    if (fileName != null && !fileName.isEmpty()) {
      return excelTemplateMapper.selectTemplateByNameAndUser(fileName, userId);
    }
    return null;
  }

  public List<ExcelApiDto.SysMetadata> getSysMetadata(String fileName, UserInfoDto userInfo) {
    String userId = userInfo != null ? userInfo.getId() : "anonymous";
    ExcelMappingTemplate savedTemplate = null;

    if (fileName != null && !fileName.isEmpty()) {
      savedTemplate = excelTemplateMapper.selectTemplateByNameAndUser(fileName, userId);
    }

    if (savedTemplate != null) {
      try {
        return objectMapper.readValue(savedTemplate.getMappingRules(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, ExcelApiDto.SysMetadata.class));
      } catch (Exception e) {
        log.error("Failed to parse saved mapping rules", e);
      }
    }

    return ExcelTemplateType.getMetadataByFileName(fileName);
  }

  public ExcelApiDto.ValidateResponse validateExcel(ExcelApiDto.ValidateRequest request) {
    String fileKey = request.getFileKey();
    String fileName = request.getFileName();
    List<ExcelApiDto.ColumnMappingDto> mappings = request.getColumnMappings();
    int dataStartRow = request.getDataStartRow();

    // structures.dataEndRow - structures.dataStartRow + 1 = 레코드 당 행 수
    // (dataEndRow < dataStartRow 인 경우 기본값 1로 처리)
    int dataRowsPerSet = Math.max(1, request.getDataEndRow() - dataStartRow + 1);

    // 파일명 기반으로 해당 텞플릿의 메타데이터 선로드
    List<ExcelApiDto.SysMetadata> templateMetadata = ExcelTemplateType.getMetadataByFileName(fileName);
    if (templateMetadata.isEmpty()) {
      log.warn("[validateExcel] 파일명 '{}'(에) 해당하는 텝플릿 메타데이터가 없습니다. 검증 건너뜀니다.", fileName);
      return new ExcelApiDto.ValidateResponse(true, "등록된 텝플릿이 없어 검증을 건너뜀니다.", new ArrayList<>());
    }
    log.info("[validateExcel] fileKey={}, fileName={}, dataStartRow={}, dataRowsPerSet={}",
        fileKey, fileName, dataStartRow, dataRowsPerSet);

    // 전체 행 조회
    List<Excel> allRows;
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      ExcelMapper mapper = sqlSession.getMapper(ExcelMapper.class);
      allRows = mapper.selectAllExcelList(fileKey);
    }

    // 헤더 행 스킵 후 rowIndex → cells 맵 구성 및 정렬된 인덱스 목록 생성
    Map<Integer, List<String>> rowMap = new HashMap<>();
    List<Integer> sortedDataRowIndices = new ArrayList<>();
    for (Excel row : allRows) {
      if (row.getRowIndex() >= dataStartRow && row.getDataJson() != null) {
        rowMap.put(row.getRowIndex(), row.getDataJson());
        sortedDataRowIndices.add(row.getRowIndex());
      }
    }
    Collections.sort(sortedDataRowIndices);

    // 검증 대상 컬럼 구분: backColumn이 있고 파일명에 일치하는 텝플릿의 metadata가 존재하는 매핑만 선별
    Map<ExcelApiDto.ColumnMappingDto, ExcelApiDto.SysMetadata> validationTargets = new LinkedHashMap<>();
    for (ExcelApiDto.ColumnMappingDto mapping : mappings) {
      String backColumn = mapping.getBackColumn();
      if (backColumn == null || backColumn.trim().isEmpty()) continue;
      ExcelApiDto.SysMetadata meta = findMetadata(backColumn, templateMetadata);
      if (meta == null) continue; // metadata 없는 컬럼(라벨/표시용)은 검증 제외
      validationTargets.put(mapping, meta);
    }
    log.info("[validateExcel] 검증 대상 컬럼 수: {}/{}", validationTargets.size(), mappings.size());

    List<ExcelApiDto.ValidationError> errors = new ArrayList<>();
    int totalDataRows = sortedDataRowIndices.size();
    int recordCount = (int) Math.ceil((double) totalDataRows / dataRowsPerSet);

    for (int recordIdx = 0; recordIdx < recordCount; recordIdx++) {

      // 이 레코드를 구성하는 행 인덱스 목록 (relativeRow 0, 1, 2 ...)
      List<Integer> recordRowIndices = new ArrayList<>();
      for (int r = 0; r < dataRowsPerSet; r++) {
        int listIdx = recordIdx * dataRowsPerSet + r;
        if (listIdx < totalDataRows) {
          recordRowIndices.add(sortedDataRowIndices.get(listIdx));
        }
      }
      if (recordRowIndices.isEmpty()) continue;

      // 에러 리포트용 대표 행 번호 (레코드의 첫 번째 행)
      int representativeRowIndex = recordRowIndices.get(0);

      for (Map.Entry<ExcelApiDto.ColumnMappingDto, ExcelApiDto.SysMetadata> entry : validationTargets.entrySet()) {
        ExcelApiDto.ColumnMappingDto mapping = entry.getKey();
        ExcelApiDto.SysMetadata meta = entry.getValue();

        // relativeRow로 이 매핑이 속한 행 선택
        int relativeRow = mapping.getRelativeRow();
        if (relativeRow >= recordRowIndices.size()) continue;

        int targetRowIndex = recordRowIndices.get(relativeRow);
        List<String> cells = rowMap.get(targetRowIndex);
        if (cells == null) continue;

        int colIndex = mapping.getColIndex();
        String val = (colIndex >= 0 && colIndex < cells.size()) ? cells.get(colIndex) : "";

        // 1. 필수값 검증
        if (meta.isRequired() && (val == null || val.trim().isEmpty())) {
          errors.add(new ExcelApiDto.ValidationError(
              representativeRowIndex, mapping.getBackColumn(),
              meta.getName() + "은(는) 필수 입력 항목입니다.", val));
          continue;
        }

        // 값이 비어있으면 이후 검증 스킵
        if (val == null || val.trim().isEmpty()) continue;

        // 2. 타입 검증
        if ("number".equalsIgnoreCase(meta.getDataType())) {
          try {
            Double.parseDouble(val.replace(",", "").trim());
          } catch (NumberFormatException e) {
            errors.add(new ExcelApiDto.ValidationError(
                representativeRowIndex, mapping.getBackColumn(),
                meta.getName() + "은(는) 숫자 형식이어야 합니다.", val));
            continue;
          }
        } else if ("date".equalsIgnoreCase(meta.getDataType())) {
          String cleanVal = val.replaceAll("[^0-9]", "").trim();
          boolean validDate = false;
          if (cleanVal.length() == 8) {
            try {
              int m = Integer.parseInt(cleanVal.substring(4, 6));
              int d = Integer.parseInt(cleanVal.substring(6, 8));
              validDate = (m >= 1 && m <= 12 && d >= 1 && d <= 31);
            } catch (Exception ignored) {}
          }
          if (!validDate) {
            errors.add(new ExcelApiDto.ValidationError(
                representativeRowIndex, mapping.getBackColumn(),
                meta.getName() + "은(는) 올바른 날짜 형식(예: YYYY-MM-DD)이어야 합니다.", val));
            continue;
          }
        }

        // 3. 정규식 검증
        if (meta.getRegex() != null && !meta.getRegex().trim().isEmpty()) {
          try {
            String regex = meta.getRegex().replace("\\\\", "\\");
            log.debug("[validateExcel] regex 검증 — col={}, val='{}', rawRegex='{}', compiledRegex='{}'",
                mapping.getBackColumn(), val, meta.getRegex(), regex);
            if (!val.trim().matches(regex)) {
              errors.add(new ExcelApiDto.ValidationError(
                  representativeRowIndex, mapping.getBackColumn(),
                  meta.getName() + " 형식이 올바르지 않습니다.", val));
            }
          } catch (Exception e) {
            log.error("Regex validation error for pattern: {}", meta.getRegex(), e);
          }
        }
      }
    }

    boolean success = errors.isEmpty();
    String message = success ? "데이터 검증에 성공했습니다." : "데이터 검증에 실패했습니다.";
    return new ExcelApiDto.ValidateResponse(success, message, errors);
  }

  /**
   * 파일명으로 선로드된 템플릿 메타데이터 목록 안에서 name(표시명) 기준으로 조회.
   * SysMetadata.backColumn 은 @JsonProperty(WRITE_ONLY) 로 프론트에 노출되지 않으므로,
   * 프론트가 ColumnMappingDto.backColumn 에 보내는 값은 name(예: "사업자번호") 이다.
   * metadata가 없는 컬럼(라벨/표시용)은 null 반환 → 검증 제외 처리됨.
   */
  private ExcelApiDto.SysMetadata findMetadata(String backColumn, List<ExcelApiDto.SysMetadata> metadataList) {
    if (backColumn == null || metadataList == null) return null;
    for (ExcelApiDto.SysMetadata meta : metadataList) {
      if (backColumn.equals(meta.getName())) { // 프론트가 보내는 표시명(name) 기준 매칭
        return meta;
      }
    }
    return null;
  }
}