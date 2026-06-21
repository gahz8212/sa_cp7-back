package com.paycoms.cp7.api.common.constant;

import com.paycoms.cp7.api.common.dto.ExcelApiDto.SysMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExcelTemplateType {
    private String templateType;
    private List<String> fileNames;
    private List<SysMetadata> metadata;

    private static final List<ExcelTemplateType> TEMPLATES = new ArrayList<>();

    static {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            try (InputStream is = ExcelTemplateType.class.getResourceAsStream("/excel-templates.json")) {
                if (is != null) {
                    List<ExcelTemplateType> list = objectMapper.readValue(is, new TypeReference<List<ExcelTemplateType>>() {});
                    TEMPLATES.addAll(list);
                } else {
                    log.error("excel-templates.json not found in resources!");
                }
            }
        } catch (Exception e) {
            log.error("Failed to load excel-templates.json", e);
        }
    }

    public static ExcelTemplateType[] values() {
        return TEMPLATES.toArray(new ExcelTemplateType[0]);
    }

    public static List<SysMetadata> getMetadataByFileName(String fileName) {
        for (ExcelTemplateType type : TEMPLATES) {
            if (type.fileNames != null && type.fileNames.contains(fileName)) {
                return type.metadata;
            }
        }
        return Collections.emptyList();
    }
}
