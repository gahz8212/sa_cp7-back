package com.paycoms.cp7.api.auth.mapper;

import com.paycoms.cp7.api.auth.model.AuthEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AuthMapper {
    List<AuthEntity> test();
}