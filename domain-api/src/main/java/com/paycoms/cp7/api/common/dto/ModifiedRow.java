package com.paycoms.cp7.api.common.dto;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

// UpdateExcelRequest.java

    @Getter @Setter
    public class ModifiedRow {
        private Integer rowIndex;
        private Map<String, Object> original; // 원본 데이터 (Map으로 유연하게 수용)
        private Map<String, Object> modified; // 수정된 데이터 (Map으로 유연하게 수용)
    }
   
    