package com.paycoms.cp7.api.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.paycoms.cp7.api.common.model.ExcelMappingTemplate;

@Mapper
public interface ExcelTemplateMapper {
    void insertTemplate(ExcelMappingTemplate template);
    void updateTemplate(ExcelMappingTemplate template);
    ExcelMappingTemplate selectTemplateBySysType(@Param("targetSysType") String targetSysType, @Param("userId") String userId);
    ExcelMappingTemplate selectTemplateByNameAndUser(@Param("templateName") String templateName, @Param("userId") String userId);
}
