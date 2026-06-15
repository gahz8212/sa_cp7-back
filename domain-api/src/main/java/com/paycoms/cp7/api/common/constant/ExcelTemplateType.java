package com.paycoms.cp7.api.common.constant;

import com.paycoms.cp7.api.common.dto.SysMetadataDto;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

public enum ExcelTemplateType {
    SIMPLE(Arrays.asList("근로자_간편서식.xlsx", "자재_간편서식.xlsx","자재_간편서식_2열.xlsx","장비_간편서식.xlsx"), Arrays.asList(
        new SysMetadataDto("companyName", "사업자명", "회사 이름", true, null, null),
        new SysMetadataDto("companyNumber", "사업자번호", "회사 등록번호", true, null, null),
        new SysMetadataDto("memberName", "회원이름", "대표자이름", true, null, null),
        new SysMetadataDto("itemName", "품목명", "품목 이름", true, null, null),
        new SysMetadataDto("phone", "연락처", "휴대폰 번호", true, null, null),
        new SysMetadataDto("amount", "청구액", "청구 액수", true, null, null),
        new SysMetadataDto("bank", "은행명", "은행 이름", true, null, null),
        new SysMetadataDto("account", "계좌번호", "계좌 번호", true, null, null)
    )),
    ETC1(Arrays.asList("자재_출력일보.xlsx"), Arrays.asList(
        new SysMetadataDto("companyName", "업체명", "회사 이름", true, null, null),
        new SysMetadataDto("areaName", "현장명", "현장 이름", true, null, null),
        new SysMetadataDto("companyNumber", "사업자등록번호", "회사 등록번호", true, null, null),
        new SysMetadataDto("memberName", "대표자명", "대표자이름", true, null, null),
        new SysMetadataDto("itemName", "품목명", "품목 이름", true, null, null),
        new SysMetadataDto("phone", "연락처", "휴대폰 번호", true, null, null),
        new SysMetadataDto("amount", "계약금", "계약 액수", true, null, null),
        new SysMetadataDto("address", "현장 주소", "현장 주소", true, null, null)
        // new SysMetadataDto("account", "계좌번호", "계좌 번호", true, null, null)
    )),
    ETC2(Arrays.asList("기타2.xlsx"), Arrays.asList(
        // new SysMetadataDto("companyName", "사업자명", "회사이름", true, null, null),
        // new SysMetadataDto("companyNumber", "사업자번호", "회사 등록번호", true, null, null),
        // new SysMetadataDto("memberName", "대표자명", "대표자명", true, null, null),
        // new SysMetadataDto("itemName", "품목명", "품목 이름", true, null, null),
        // new SysMetadataDto("bank", "은행명", "은행 이름", true, null, null),
        // new SysMetadataDto("amount", "청구액수", "청구 액수", true, null, null),
        // new SysMetadataDto("phone", "연락처", "휴대폰 번호", true, null, null),
        // new SysMetadataDto("account", "계좌번호", "계좌 번호", true, null, null)
    ));

    private final List<String> fileNames;
    private final List<SysMetadataDto> metadata;

    ExcelTemplateType(List<String> fileNames, List<SysMetadataDto> metadata) {
        this.fileNames = fileNames;
        this.metadata = metadata;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public List<SysMetadataDto> getMetadata() {
        return metadata;
    }

    public static List<SysMetadataDto> getMetadataByFileName(String fileName) {
        for (ExcelTemplateType type : values()) {
            if (type.fileNames.contains(fileName)) {
                return type.metadata;
            }
        }
        return Collections.emptyList();
    }
}
