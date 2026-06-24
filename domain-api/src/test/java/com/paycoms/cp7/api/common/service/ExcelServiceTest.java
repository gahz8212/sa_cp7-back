package com.paycoms.cp7.api.common.service;

import com.paycoms.cp7.api.common.mapper.ExcelMapper;
import com.paycoms.cp7.api.common.model.Excel;
import com.paycoms.cp7.api.common.dto.ExcelApiDto;
import com.paycoms.cp7.api.common.constant.ExcelTemplateType;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;

@ExtendWith(MockitoExtension.class)
class ExcelServiceTest {

    @Mock
    private SqlSessionFactory sqlSessionFactory;

    @Mock
    private SqlSession sqlSession;

    @Mock
    private ExcelMapper excelMapper;

    @InjectMocks
    private ExcelService excelService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void uploadExcel_shouldProcessAndSaveData() throws IOException {
        // Given
        when(sqlSessionFactory.openSession(any(ExecutorType.class))).thenReturn(sqlSession);
        when(sqlSession.getMapper(ExcelMapper.class)).thenReturn(excelMapper);

        String createKeyString = "testKey";
        int sheetNo = 0;

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TestSheet");

        // Header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Header1");
        headerRow.createCell(1).setCellValue("Header2");

        // Data row 1
        Row dataRow1 = sheet.createRow(1);
        dataRow1.createCell(0).setCellValue("Value1-1");
        dataRow1.createCell(1).setCellValue("Value1-2");

        // Data row 2
        Row dataRow2 = sheet.createRow(2);
        dataRow2.createCell(0).setCellValue("Value2-1");
        dataRow2.createCell(1).setCellValue("Value2-2");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bos.toByteArray()
        );

        // When
        assertDoesNotThrow(() -> excelService.uploadExcel(createKeyString, mockFile, sheetNo));

        // Then
        verify(excelMapper, times(3)).insertExcel(any(Excel.class)); // Expect 3 inserts (1 header row + 2 data rows)
        verify(sqlSession).flushStatements();
        verify(sqlSession).commit();

        // Optional: Capture arguments to assert specific Excel objects
        // ArgumentCaptor<Excel> excelCaptor = ArgumentCaptor.forClass(Excel.class);
        // verify(excelMapper, times(2)).insertExcel(excelCaptor.capture());
        // List<Excel> capturedExcels = excelCaptor.getAllValues();
        // assertEquals("Value1-1", capturedExcels.get(0).getExcelData().get("Header1"));
        // assertEquals("Value2-2", capturedExcels.get(1).getExcelData().get("Header2"));
    }

    @Test
    void uploadExcel_shouldHandleEmptyFile() throws IOException {
        // Given
        String createKeyString = "testKey";
        int sheetNo = 0;

        Workbook workbook = new XSSFWorkbook();
        workbook.createSheet("EmptySheet");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "empty.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bos.toByteArray()
        );

        // When
        assertDoesNotThrow(() -> excelService.uploadExcel(createKeyString, mockFile, sheetNo));

        // Then
        verify(excelMapper, never()).insertExcel(any(Excel.class));
        verify(sqlSession, never()).flushStatements();
        verify(sqlSession, never()).commit();
    }

    @Test
    void uploadExcel_shouldHandleLargeBatch() throws IOException {
        // Given
        when(sqlSessionFactory.openSession(any(ExecutorType.class))).thenReturn(sqlSession);
        when(sqlSession.getMapper(ExcelMapper.class)).thenReturn(excelMapper);

        String createKeyString = "testKey";
        int sheetNo = 0;
        int numberOfRecords = 1001; // More than one batch (batch size 1000 in service)

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TestSheet");

        // Header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");

        // Data rows
        for (int i = 1; i <= numberOfRecords; i++) {
            Row dataRow = sheet.createRow(i);
            dataRow.createCell(0).setCellValue("ID_" + i);
            dataRow.createCell(1).setCellValue("Name_" + i);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "large_test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                bos.toByteArray()
        );

        // When
        assertDoesNotThrow(() -> excelService.uploadExcel(createKeyString, mockFile, sheetNo));

        // Then
        // Expect at least two flushes and commits for 1001 records (1000 + 1) + 1 header
        verify(excelMapper, times(numberOfRecords + 1)).insertExcel(any(Excel.class));
        verify(sqlSession, atLeast(2)).flushStatements();
        verify(sqlSession, atLeast(2)).commit();
    }

    @Test
    void validateExcel_shouldValidateCorrectly() {
        // Given
        when(sqlSessionFactory.openSession()).thenReturn(sqlSession);
        when(sqlSession.getMapper(ExcelMapper.class)).thenReturn(excelMapper);

        Excel row1 = new Excel();
        row1.setRowIndex(0);
        // Valid business number and valid phone, valid number, valid account
        row1.setDataJson(List.of("회사A", "123-45-67890", "010-1234-5678", "50000", "123-456"));

        Excel row2 = new Excel();
        row2.setRowIndex(1);
        // Invalid business number (wrong format), empty phone (required), invalid amount (not number)
        row2.setDataJson(List.of("회사B", "123-45-abcde", "", "오만원", "123-456"));

        when(excelMapper.selectAllExcelList("testKey")).thenReturn(List.of(row1, row2));

        ExcelApiDto.ValidateRequest request = new ExcelApiDto.ValidateRequest();
        request.setFileKey("testKey");
        request.setFileName("근로자_간편서식.xlsx");

        ExcelApiDto.ColumnMappingDto map1 = new ExcelApiDto.ColumnMappingDto();
        map1.setColIndex(0);
        map1.setBackColumn("사업자명");

        ExcelApiDto.ColumnMappingDto map2 = new ExcelApiDto.ColumnMappingDto();
        map2.setColIndex(1);
        map2.setBackColumn("사업자번호");

        ExcelApiDto.ColumnMappingDto map3 = new ExcelApiDto.ColumnMappingDto();
        map3.setColIndex(2);
        map3.setBackColumn("연락처");

        ExcelApiDto.ColumnMappingDto map4 = new ExcelApiDto.ColumnMappingDto();
        map4.setColIndex(3);
        map4.setBackColumn("청구액");

        ExcelApiDto.ColumnMappingDto map5 = new ExcelApiDto.ColumnMappingDto();
        map5.setColIndex(4);
        map5.setBackColumn("계좌번호");

        request.setColumnMappings(List.of(map1, map2, map3, map4, map5));

        // When
        ExcelApiDto.ValidateResponse response = excelService.validateExcel(request);

        // Then
        org.junit.jupiter.api.Assertions.assertFalse(response.isSuccess());
        // row2 should have 3 errors: companyNumber (format), phone (required), amount (number format)
        org.junit.jupiter.api.Assertions.assertEquals(3, response.getErrors().size());
        
        ExcelApiDto.ValidationError err1 = response.getErrors().stream()
                .filter(e -> "사업자번호".equals(e.getColumnCode()))
                .findFirst().orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("사업자번호 형식이 올바르지 않습니다.", err1.getErrorMessage());

        ExcelApiDto.ValidationError err2 = response.getErrors().stream()
                .filter(e -> "연락처".equals(e.getColumnCode()))
                .findFirst().orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("연락처은(는) 필수 입력 항목입니다.", err2.getErrorMessage());

        ExcelApiDto.ValidationError err3 = response.getErrors().stream()
                .filter(e -> "청구액".equals(e.getColumnCode()))
                .findFirst().orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("청구액은(는) 숫자 형식이어야 합니다.", err3.getErrorMessage());
    }
}
