package com.paycoms.cp7.api.common.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.paycoms.cp7.api.common.dto.DownloadExcelRequest;
import com.paycoms.cp7.api.common.dto.ExcelDto;
import com.paycoms.cp7.api.common.mapper.ExcelMapper;
import com.paycoms.cp7.api.common.model.Excel;
import com.paycoms.cp7.global.error.BusinessException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExcelService {

  private final SqlSessionFactory sqlSessionFactory;

  public void uploadExcel(String createKeyString, MultipartFile file, int sheetNo, int rowNo) throws IOException {

    // 엑셀 워크북 열기 (XLSX 대응)
    Workbook workbook = new XSSFWorkbook(file.getInputStream());
    Sheet sheet = workbook.getSheetAt(sheetNo);

    try {
      List<Excel> dataList = new ArrayList<>();

      // 1. 헤더 처리 (rowNo 행)
      Row headerRow = sheet.getRow(rowNo);
      List<String> headers = new ArrayList<>();
      if (headerRow != null) {
        for (int j = 0; j < headerRow.getLastCellNum(); j++) {
          Cell cell = headerRow.getCell(j);
          headers.add(cell == null ? "" : cell.getStringCellValue());
        }
        
        Excel headerExcel = new Excel();
        headerExcel.setFileKey(createKeyString);
        headerExcel.setRowType("HEADER");
        headerExcel.setRowIndex(rowNo);
        headerExcel.setDataJson(headers);
        dataList.add(headerExcel);
      }

      // 2. 데이터 처리 (rowNo + 1 행부터)
      for (int i = rowNo + 1; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        if (row == null) continue;

        List<String> rowData = new ArrayList<>();
        for (int j = 0; j < headers.size(); j++) {
          Cell cell = row.getCell(j);
          String value = "";
          if (cell != null) {
            switch (cell.getCellType()) {
              case STRING: value = cell.getStringCellValue(); break;
              case NUMERIC: value = String.valueOf(cell.getNumericCellValue()); break;
              case BOOLEAN: value = String.valueOf(cell.getBooleanCellValue()); break;
              default: value = "";
            }
          }
          rowData.add(value);
        }

        Excel dataExcel = new Excel();
        dataExcel.setFileKey(createKeyString);
        dataExcel.setRowType("DATA");
        dataExcel.setRowIndex(i);
        dataExcel.setDataJson(rowData);
        dataList.add(dataExcel);

        // 1000개마다 또는 마지막 행일 때 데이터 처리 (DB 저장)
        if (dataList.size() >= 1000 || i == sheet.getLastRowNum()) {
          saveExcels(new ArrayList<>(dataList));
          dataList.clear();
        }
      }
    } finally {
      workbook.close();
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

  public byte[] downloadExcel(DownloadExcelRequest request) throws IOException {
    // Jxls 템플릿 기반 다운로드 로직 (새로운 방식)
    /* 템플릿 파일이 resources 하위에 있다고 가정 */
    /*
    try (InputStream is = new ClassPathResource("templates/excel/" + request.getTemplateName()).getInputStream();
         ByteArrayOutputStream os = new ByteArrayOutputStream()) {
        Context context = new Context();
        context.putVar("items", request.getDataList()); // 데이터 주입
        JxlsHelper.getInstance().processTemplate(is, os, context);
        return os.toByteArray();
    }
    */
    
    // 아래는 기존 POI 수동 생성 로직 (주석 처리 예정이나 일단 구조 유지를 위해 대체 코드로 작성)
    try (Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        POIFSFileSystem fs = new POIFSFileSystem()) {
      Sheet sheet = workbook.createSheet(request.getSheetName());

      // 헤더 스타일 설정
      CellStyle headerStyle = workbook.createCellStyle();
      headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
      headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      Font font = workbook.createFont();
      font.setBold(true);
      headerStyle.setFont(font);

      String[] columns = request.getFileds();
      // 헤더 생성
      if (columns != null) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
          Cell cell = headerRow.createCell(i);
          cell.setCellValue(columns[i]);
          cell.setCellStyle(headerStyle);
        }
      }

      // 데이터 주입 부분 (JSON 구조에 맞게 수정 필요)
      /* 기존 Object[] 기반 로직 주석 처리
      List<Object[]> dataList = new ArrayList<Object[]>();
      Object[] objt = { "1", "홍길동", 1000, "2323" };
      dataList.add(objt);

      int rowIdx = 1;
      int columnNo = 0;
      for (Object[] Objx : dataList) {
        columnNo = 0;
        Row row = sheet.createRow(rowIdx++);
        for (Object item : Objx) {
          row.createCell(columnNo++).setCellValue(item.toString());
        }
      }
      */

      // 컬럼 너비 자동 조절
      /*
      if (columns != null) {
        for (int i = 0; i <= columns.length; i++) {
          sheet.autoSizeColumn(i);
        }
      }
      */

      workbook.write(out);
      
      // (이하 암호화 로직은 유지)

      if (request.getPasswordYn().equals("Y")) {
        // 암호화 설정 (Standard 모드 혹은 Agile 모드)
        EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);
        Encryptor encryptor = info.getEncryptor();
        encryptor.confirmPassword(request.getPassword());

        // 암호화된 스트림에 데이터 쓰기
        try (OutputStream os = encryptor.getDataStream(fs)) {
          os.write(out.toByteArray());
        } catch (GeneralSecurityException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        fs.writeFilesystem(resultStream);
        return resultStream.toByteArray();
      } else {
        return out.toByteArray();
      }
    }
  }
}
