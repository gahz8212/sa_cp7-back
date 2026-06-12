package com.paycoms.cp7.api.common.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
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

  public void uploadExcel(String createKeyString, MultipartFile file, int sheetNo, int rowNo) throws IOException {
    Workbook workbook = WorkbookFactory.create(file.getInputStream());
    Sheet sheet = workbook.getSheetAt(sheetNo);

    try {
      List<Excel> dataList = new ArrayList<>();
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
        for (int j = 0; j < headers.size(); j++) {
          Cell cell = (row != null) ? row.getCell(j) : null;
          rowData.add(cell != null ? getCellValueAsString(cell) : "");
        }
        Excel excelObj = new Excel();
        excelObj.setFileKey(createKeyString);
        excelObj.setRowType(i == bestRowNo ? "HEADER" : "DATA");
        excelObj.setRowIndex(i);
        excelObj.setDataJson(rowData);
        dataList.add(excelObj);

        if (dataList.size() >= 1000 || i == sheet.getLastRowNum()) {
          saveExcels(new ArrayList<>(dataList));
          dataList.clear();
        }
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
      if (row == null) continue;
      int currentCells = 0;
      for (int j = 0; j < row.getLastCellNum(); j++) {
        Cell cell = row.getCell(j);
        if (cell != null && !getCellValueAsString(cell).trim().isEmpty()) currentCells++;
      }
      if (currentCells > maxCells) {
        maxCells = currentCells;
        bestRowIndex = i;
      }
    }
    return bestRowIndex;
  }

  private String getCellValueAsString(Cell cell) {
    if (cell == null) return "";
    switch (cell.getCellType()) {
      case STRING: return cell.getStringCellValue();
      case NUMERIC: return String.valueOf(cell.getNumericCellValue());
      case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
      case FORMULA: return cell.getCellFormula();
      default: return "";
    }
  }

  public void updateModifiedRows(UserInfoDto userInfo, List<ModifiedRow> changes) {
    for (ModifiedRow row : changes) {
      log.info("행 {}번 업데이트 수행: {}", row.getRowIndex(), row.getModified());
    }
  }

  @Transactional
  public void saveExcelDataAndTemplate(UserInfoDto userInfo, SaveExcelDataAndTemplateRequestDto request) {
    if (request.getModifiedRows() != null) {
      updateModifiedRows(userInfo, request.getModifiedRows());
    }

    if (request.getTemplate() != null) {
      SaveExcelDataAndTemplateRequestDto.TemplateDto templateDto = request.getTemplate();
      String userId = userInfo != null ? userInfo.getId() : "anonymous";
      String sysType = templateDto.getTargetSysType();

      ExcelMappingTemplate existing = excelTemplateMapper.selectTemplateBySysType(sysType, userId);
      
      ExcelMappingTemplate template = new ExcelMappingTemplate();
      template.setTemplateName(sysType + " 양식");
      template.setTargetSysType(sysType);
      template.setUserId(userId);
      
      try {
        template.setHeaderStructure(objectMapper.writeValueAsString(templateDto.getHeaderStructure()));
        template.setMappingRules(objectMapper.writeValueAsString(templateDto.getTargetColumns()));
      } catch (Exception e) {
        throw new BusinessException("COMM_001", "JSON 변환 오류");
      }

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

  public List<SysMetadataDto> getSysMetadata(String targetType, UserInfoDto userInfo) {
    String userId = userInfo != null ? userInfo.getId() : "anonymous";
    ExcelMappingTemplate savedTemplate = excelTemplateMapper.selectTemplateBySysType(targetType, userId);

    if (savedTemplate != null) {
      try {
        return objectMapper.readValue(savedTemplate.getMappingRules(), 
            objectMapper.getTypeFactory().constructCollectionType(List.class, SysMetadataDto.class));
      } catch (Exception e) {
        log.error("Failed to parse saved mapping rules", e);
      }
    }

    List<SysMetadataDto> list = new ArrayList<>();
    if ("EMPLOYEE".equalsIgnoreCase(targetType)) {
      list.add(new SysMetadataDto("companyName", "사업자명", "회사 이름", true, null, null));
      list.add(new SysMetadataDto("companyNumber", "사업자번호", "회사 등록번호", true, null, null));
      list.add(new SysMetadataDto("memberName", "회원이름", "대표자이름", true, null, null));
      list.add(new SysMetadataDto("itemName", "품목명", "품목 이름", true, null, null));
      list.add(new SysMetadataDto("phone", "연락처", "휴대폰 번호", true, null, null));
      list.add(new SysMetadataDto("amount", "청구액", "청구 액수", true, null, null));
      list.add(new SysMetadataDto("bank", "은행명", "은행 이름", true, null, null));
      list.add(new SysMetadataDto("account", "계좌번호", "계좌 번호", true, null, null));
    } else if ("PAYROLL".equalsIgnoreCase(targetType)) {
      list.add(new SysMetadataDto("memberId", "사번", "사번", true, null, null));
      list.add(new SysMetadataDto("basePay", "기본급", "기본급여", true, null, null));
    }
    return list;
  }

  public String getExactSysTypeByFileName(String fileName) {
    if (fileName == null) return "UNKNOWN";
    if (fileName.contains("근로자")) return "EMPLOYEE";
    if (fileName.contains("급여")) return "PAYROLL";
    return "UNKNOWN";
  }
}