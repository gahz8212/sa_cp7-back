package com.paycoms.cp7.api.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.paycoms.cp7.api.common.model.Excel;
import java.util.List;

@Mapper
public interface ExcelMapper {
  void insertExcel(Excel excel);
  List<Excel> selectExcelList(@Param("fileKey") String fileKey, @Param("limit") int limit, @Param("offset") int offset);
  int selectExcelCount(@Param("fileKey") String fileKey);
}
