package com.paycoms.cp7.api.common.dto;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

// UpdateExcelRequest.java
     @Getter @Setter
     public class UpdateExcelRequest {
         @NotEmpty(message = "수정된 데이터 내역이 비어있습니다.")
         private List<ModifiedRow> modifiedRows;
    
         // 필요 시 추가 정보 (예: 파일 ID, 시트 번호 등)
         private String fileId;
     }
   
    // ModifiedRow.java
   