package com.paycoms.cp7.api.common.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
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
    ExcelMapper mapper = sqlSessionFactory.openSession().getMapper(ExcelMapper.class);
    return mapper.selectExcelList(fileKey, size, (page - 1) * size);
  }

  @Transactional(readOnly = true)
  public int getExcelCount(String fileKey) {
    ExcelMapper mapper = sqlSessionFactory.openSession().getMapper(ExcelMapper.class);
    return mapper.selectExcelCount(fileKey);
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
    List<ExcelApiDto.ColumnMappingDto> mappings = request.getColumnMappings();

    List<Excel> dataList;
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      ExcelMapper mapper = sqlSession.getMapper(ExcelMapper.class);
      dataList = mapper.selectAllExcelList(fileKey);
    }

    List<ExcelApiDto.ValidationError> errors = new ArrayList<>();

    for (Excel excelRow : dataList) {
      int rowIndex = excelRow.getRowIndex();
      if (rowIndex < request.getDataStartRow()) {
        continue;
      }
      List<String> cells = excelRow.getDataJson();
      if (cells == null) continue;

      for (ExcelApiDto.ColumnMappingDto mapping : mappings) {
        int colIndex = mapping.getColIndex();
        String backColumn = mapping.getBackColumn();

        String val = "";
        if (colIndex >= 0 && colIndex < cells.size()) {
          val = cells.get(colIndex);
        }

        // Try to get metadata rules
        ExcelApiDto.SysMetadata meta = findMetadata(backColumn);
        if (meta == null) {
          // If metadata not found, we skip validation
          continue;
        }

        // 1. Required Check
        if (meta.isRequired()) {
          if (val == null || val.trim().isEmpty()) {
            errors.add(new ExcelApiDto.ValidationError(rowIndex, backColumn, meta.getName() + "은(는) 필수 입력 항목입니다.", val));
            continue;
          }
        }

        // Skip other validations if value is empty and not required
        if (val == null || val.trim().isEmpty()) {
          continue;
        }

        // 2. Data Type Check
        if ("number".equalsIgnoreCase(meta.getDataType())) {
          try {
            String cleanVal = val.replace(",", "").trim();
            Double.parseDouble(cleanVal);
          } catch (NumberFormatException e) {
            errors.add(new ExcelApiDto.ValidationError(rowIndex, backColumn, meta.getName() + "은(는) 숫자 형식이어야 합니다.", val));
            continue;
          }
        } else if ("date".equalsIgnoreCase(meta.getDataType())) {
          String cleanVal = val.replaceAll("[^0-9]", "").trim();
          boolean validDate = false;
          if (cleanVal.length() == 8) {
            try {
              int y = Integer.parseInt(cleanVal.substring(0, 4));
              int m = Integer.parseInt(cleanVal.substring(4, 6));
              int d = Integer.parseInt(cleanVal.substring(6, 8));
              if (m >= 1 && m <= 12 && d >= 1 && d <= 31) {
                validDate = true;
              }
            } catch (Exception ignored) {}
          }
          if (!validDate) {
            errors.add(new ExcelApiDto.ValidationError(rowIndex, backColumn, meta.getName() + "은(는) 올바른 날짜 형식(예: YYYY-MM-DD)이어야 합니다.", val));
            continue;
          }
        }

        // 3. Regex Check
        if (meta.getRegex() != null && !meta.getRegex().trim().isEmpty()) {
          try {
            String regex = meta.getRegex().replace("\\\\", "\\");
            if (!val.trim().matches(regex)) {
              errors.add(new ExcelApiDto.ValidationError(rowIndex, backColumn, meta.getName() + " 형식이 올바르지 않습니다.", val));
              continue;
            }
          } catch (Exception e) {
            log.error("Regex validation error for pattern: " + meta.getRegex(), e);
          }
        }
      }
    }

    boolean success = errors.isEmpty();
    String message = success ? "데이터 검증에 성공했습니다." : "데이터 검증에 실패했습니다.";
    return new ExcelApiDto.ValidateResponse(success, message, errors);
  }

  private ExcelApiDto.SysMetadata findMetadata(String backColumn) {
    if (backColumn == null) return null;
    for (ExcelTemplateType type : ExcelTemplateType.values()) {
      if (type.getMetadata() != null) {
        for (ExcelApiDto.SysMetadata meta : type.getMetadata()) {
          if (backColumn.equals(meta.getName())) {
            return meta;
          }
        }
      }
    }
    return null;
  }
}