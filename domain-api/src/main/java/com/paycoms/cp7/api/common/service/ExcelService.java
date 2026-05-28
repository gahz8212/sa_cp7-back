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
    Sheet sheet = workbook.getSheetAt(sheetNo); // 지정된 시트 사용

    try {
      List<ExcelDto> dataList = new ArrayList<>();

      int no = 1; // 고유 번호 시작값 (예: 시퀀스 번호)

      for (int i = rowNo; i <= sheet.getLastRowNum(); i++) { // 지정된 행 번호부터 마지막 열 번호까지
        Row row = sheet.getRow(i);
        if (row == null)
          continue;

        ExcelDto excelDto = new ExcelDto();

        excelDto.setNo(no++);

        for (int j = 0; j < 50; j++) {
          String fieldName = String.format("cell%02d", j + 1); // cell01, cell02 ... 생성
          String cellValue = row.getCell(j) == null ? null : row.getCell(j).getStringCellValue(); // 셀

          try {
            // 리플렉션으로 해당 이름의 필드를 찾아 값을 세팅
            Field field = ExcelDto.class.getDeclaredField(fieldName);
            field.setAccessible(true); // private 필드 접근 허용
            field.set(excelDto, cellValue);
          } catch (NoSuchFieldException | IllegalAccessException e) {
            // 필드가 없거나 접근 불가할 경우 예외 처리
            throw new BusinessException("EXCEL_002", fieldName);
          }
        }

        dataList.add(excelDto);

        // 1000개마다 또는 마지막 행일 때 데이터 처리 (DB 저장)
        if (i % 1000 == 0 || i == sheet.getLastRowNum()) {
          List<Excel> excels = dataList.stream().map(dto -> {
            Excel excel = new Excel();
            excel.setCeateKeyString(createKeyString);
            excel.setCreateKeyNo(dto.getNo());
            // DTO의 필드 값을 엔티티로 매핑
            // 1부터 50까지 반복하며 필드 값을 복사
            for (int j = 1; j <= 50; j++) {
              String fieldName = String.format("cell%02d", j);

              try {
                // DTO에서 값 꺼내기 (Field 이용)
                Field dtoField = dto.getClass().getDeclaredField(fieldName);
                dtoField.setAccessible(true);
                Object value = dtoField.get(dto);

                // Entity에 값 넣기 (Field 이용)
                Field entityField = excel.getClass().getDeclaredField(fieldName);
                entityField.setAccessible(true);
                entityField.set(excel, value);
              } catch (Exception e) {
                // 필드가 없거나 접근 오류 시 로그 출력
                throw new BusinessException("EXCEL_002", fieldName);
              }
            }

            return excel;
          }).toList();
          dataList.clear(); // 리스트 초기화

          saveExcels(excels); // DB 저장 (배치 모드)
        }      }
    } finally {
      workbook.close();
    }
  }

  @Transactional
  public void saveExcels(List<Excel> excelList) {
    // BATCH 모드로 세션을 엽니다. 대용량 데이터 insert로 메모리에 담아 한번에 처리
    try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
      ExcelMapper mapper = sqlSession.getMapper(ExcelMapper.class);

      for (Excel excel : excelList) {
        // mapper.insertExcel(excel); // 실제 DB로 바로 안 가고 메모리에 쌓임
      }

      // 쌓인 모든 쿼리를 한 번에 실행
      sqlSession.flushStatements();

      sqlSession.commit(); // 트랜잭션 커밋

    } catch (Exception e) {
      throw new BusinessException("EXCEL_003", e.getMessage());
    }
  }

  public byte[] downloadExcel(DownloadExcelRequest request) throws IOException {
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

      // 컬럼 너비 자동 조절
      if (columns != null) {
        for (int i = 0; i <= columnNo; i++) {
          sheet.autoSizeColumn(i);
        }
      }

      workbook.write(out);

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
