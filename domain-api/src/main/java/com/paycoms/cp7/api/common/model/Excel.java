package com.paycoms.cp7.api.common.model;

import lombok.*;
import org.apache.ibatis.type.Alias;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Alias("Excel")
public class Excel {
    private String fileKey;    // file_key
    private String rowType;    // row_type (HEADER, DATA)
    private int rowIndex;      // row_index
    private List<String> dataJson; // data_json (JSON Array)
}
