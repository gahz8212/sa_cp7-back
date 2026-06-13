package com.paycoms.cp7.api.common.constant;

import com.paycoms.cp7.api.common.dto.SysMetadataDto;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

public enum ExcelTemplateType {
    WORKER("근로자_간편서식.xlsx", Arrays.asList(
        new SysMetadataDto("companyName", "사업자명", "회사 이름", true, null, null),
        new SysMetadataDto("companyNumber", "사업자번호", "회사 등록번호", true, null, null),
        new SysMetadataDto("memberName", "회원이름", "대표자이름", true, null, null),
        new SysMetadataDto("itemName", "품목명", "품목 이름", true, null, null),
        new SysMetadataDto("phone", "연락처", "휴대폰 번호", true, null, null),
        new SysMetadataDto("amount", "청구액", "청구 액수", true, null, null),
        new SysMetadataDto("bank", "은행명", "은행 이름", true, null, null),
        new SysMetadataDto("account", "계좌번호", "계좌 번호", true, null, null)
    )),
    EQUIPMENT("장비_간편서식.xlsx", Arrays.asList(
        new SysMetadataDto("companyName", "사업자명", "회사이름", true, null, null),
        new SysMetadataDto("companyNumber", "사업자번호", "회사 등록번호", true, null, null),
        new SysMetadataDto("memberName", "대표자명", "대표자명", true, null, null),
        new SysMetadataDto("itemName", "품목명", "품목 이름", true, null, null),
        new SysMetadataDto("bank", "은행명", "은행 이름", true, null, null),
        new SysMetadataDto("amount", "청구액수", "청구 액수", true, null, null),
        new SysMetadataDto("phone", "연락처", "휴대폰 번호", true, null, null),
        new SysMetadataDto("account", "계좌번호", "계좌 번호", true, null, null)
    ));

    private final String fileName;
    private final List<SysMetadataDto> metadata;

    ExcelTemplateType(String fileName, List<SysMetadataDto> metadata) {
        this.fileName = fileName;
        this.metadata = metadata;
    }

    public String getFileName() {
        return fileName;
    }

    public List<SysMetadataDto> getMetadata() {
        return metadata;
    }

    public static List<SysMetadataDto> getMetadataByFileName(String fileName) {
        for (ExcelTemplateType type : values()) {
            if (type.fileName.equals(fileName)) {
                return type.metadata;
            }
        }
        return Collections.emptyList();
    }
}
