package com.paycoms.cp7.api.common.dto;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "파일 업로드 요청 DTO")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근 제어 (보안 및 지연로딩 대비)
public class UploadFileRequest {
    @SuppressWarnings("deprecation")
    @Schema(description = "파일정보(다중)", required = true, nullable = false)
    @NotNull(message = "VALI_001.files")
    private List<MultipartFile> files;

    @SuppressWarnings("deprecation")
    @Schema(description = "업로드 경로", required = false, nullable = true, example = "image/")
    private String uploadPath;
}