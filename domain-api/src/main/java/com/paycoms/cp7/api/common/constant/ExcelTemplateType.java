package com.paycoms.cp7.api.common.constant;

import com.paycoms.cp7.api.common.dto.ExcelApiDto.SysMetadata;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.paycoms.cp7.api.common.dto.ExcelApiDto;
//같은 컬럼을 사용하는 엑셀파일이 여러개 있을 수 있기 때문에, 엑셀파일명과 매핑되는 컬럼정보를 enum으로 관리

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ExcelTemplateType {
    SIMPLE(Arrays.asList("근로자_간편서식.xlsx", "자재_간편서식.xlsx","자재_간편서식_2열.xlsx","장비_간편서식.xlsx"), Arrays.asList(
        new ExcelApiDto.SysMetadata("companyName", "사업자명", "회사 이름", true, null, null),
        new ExcelApiDto.SysMetadata("companyNumber", "사업자번호", "회사 등록번호", true, null, null),
        new ExcelApiDto.SysMetadata("memberName", "회원이름", "대표자이름", true, null, null),
        new ExcelApiDto.SysMetadata("itemName", "품목명", "품목 이름", true, null, null),
        new ExcelApiDto.SysMetadata("phone", "연락처", "휴대폰 번호", true, null, null),
        new ExcelApiDto.SysMetadata("amount", "청구액", "청구 액수", true, null, null),
        new ExcelApiDto.SysMetadata("bank", "은행명", "은행 이름", true, null, null),
        new ExcelApiDto.SysMetadata("account", "계좌번호", "계좌 번호", true, null, null)
    )),
    ETC1(Arrays.asList("자재_출력일보.xlsx"), Arrays.asList(
        new ExcelApiDto.SysMetadata("companyName", "업체명", "회사 이름", true, null, null),
        new ExcelApiDto.SysMetadata("areaName", "현장명", "현장 이름", true, null, null),
        new ExcelApiDto.SysMetadata("companyNumber", "사업자등록번호", "회사 등록번호", true, null, null),
        new ExcelApiDto.SysMetadata("memberName", "대표자명", "대표자이름", true, null, null),
        new ExcelApiDto.SysMetadata("itemName", "품목명", "품목 이름", true, null, null),
        new ExcelApiDto.SysMetadata("phone", "연락처", "휴대폰 번호", true, null, null),
        new ExcelApiDto.SysMetadata("amount", "계약금", "계약 액수", true, null, null),
        new ExcelApiDto.SysMetadata("address", "현장 주소", "현장 주소", true, null, null)
        // new ExcelApiDto.SysMetadata("account", "계좌번호", "계좌 번호", true, null, null)
    )),
    ETC2(Arrays.asList("기타2.xlsx"), Arrays.asList(
        // new ExcelApiDto.SysMetadata("companyName", "사업자명", "회사이름", true, null, null),
        // new ExcelApiDto.SysMetadata("companyNumber", "사업자번호", "회사 등록번호", true, null, null),
        // new ExcelApiDto.SysMetadata("memberName", "대표자명", "대표자명", true, null, null),
        // new ExcelApiDto.SysMetadata("itemName", "품목명", "품목 이름", true, null, null),
        // new ExcelApiDto.SysMetadata("bank", "은행명", "은행 이름", true, null, null),
        // new ExcelApiDto.SysMetadata("amount", "청구액수", "청구 액수", true, null, null),
        // new ExcelApiDto.SysMetadata("phone", "연락처", "휴대폰 번호", true, null, null),
        // new ExcelApiDto.SysMetadata("account", "계좌번호", "계좌 번호", true, null, null)
    ));

    private final List<String> fileNames;
    private final List<ExcelApiDto.SysMetadata> metadata;

    ExcelTemplateType(List<String> fileNames, List<ExcelApiDto.SysMetadata> metadata) {
        this.fileNames = fileNames;
        this.metadata = metadata;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public List<ExcelApiDto.SysMetadata> getMetadata() {
        return metadata;
    }

    public String getTemplateType() {
        return this.name();
    }

    public static List<ExcelApiDto.SysMetadata> getMetadataByFileName(String fileName) {
        for (ExcelTemplateType type : values()) {
            if (type.fileNames.contains(fileName)) {
                return type.metadata;
            }
        }
        return Collections.emptyList();
    }
}
