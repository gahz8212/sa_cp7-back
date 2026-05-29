package com.paycoms.cp7.api.common.service;

import com.paycoms.cp7.api.common.mapper.ExcelMapper;
import com.paycoms.cp7.api.common.model.Excel;
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
        int rowNo = 0; // Header on the first row

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
        assertDoesNotThrow(() -> excelService.uploadExcel(createKeyString, mockFile, sheetNo, rowNo));

        // Then
        verify(excelMapper, times(2)).insertExcel(any(Excel.class)); // Expect 2 inserts for 2 data rows
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
        int rowNo = 0;

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
        assertDoesNotThrow(() -> excelService.uploadExcel(createKeyString, mockFile, sheetNo, rowNo));

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
        int rowNo = 0;
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
        assertDoesNotThrow(() -> excelService.uploadExcel(createKeyString, mockFile, sheetNo, rowNo));

        // Then
        // Expect at least two flushes and commits for 1001 records (1000 + 1)
        verify(excelMapper, times(numberOfRecords)).insertExcel(any(Excel.class));
        verify(sqlSession, atLeast(2)).flushStatements();
        verify(sqlSession, atLeast(2)).commit();
    }
}
