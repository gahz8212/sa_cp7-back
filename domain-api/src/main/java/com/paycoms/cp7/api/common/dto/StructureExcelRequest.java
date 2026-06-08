package com.paycoms.cp7.api.common.dto;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// StructureExcelRequest.java
@Getter
@Setter
@NoArgsConstructor
public class StructureExcelRequest {
    // 엑셀에서 추출한 각 영역의 데이터를 직접 담습니다.
    @NotEmpty(message = "헤더 구조 데이터가 비어있습니다.")
    private List<List<String>> flattenedHeaders;

    @NotEmpty(message = "샘플 데이터 구조가 비어있습니다.")
    private List<List<String>> flattenedData;

    // ETC는 비어있을 수도 있으므로 Validation 조건에 따라 조절하세요.
    private List<List<String>> flattenedEtc;

    private String fileId;
}

// Structures.java
