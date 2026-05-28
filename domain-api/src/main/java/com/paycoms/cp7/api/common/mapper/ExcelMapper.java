package com.paycoms.cp7.api.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.paycoms.cp7.api.common.model.Excel;

@Mapper
public interface ExcelMapper {
  void insertExcel(Excel excel);
}
