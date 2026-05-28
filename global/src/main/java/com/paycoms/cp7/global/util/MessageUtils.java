package com.paycoms.cp7.global.util;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import com.paycoms.cp7.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageUtils {
    private final MessageSource messageSource;

    @SuppressWarnings("unchecked")
    public <T> ApiResponse<T> createResponse(String code, T data, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();

        // 상태 코드 가져오기 (S002.status) - 기본값 200
        String statusStr = messageSource.getMessage(code + ".status", null, "200", locale);
        int status = Integer.parseInt(statusStr);

        // 메시지 가져오기 (S002.message)
        String translatedMessage = messageSource.getMessage(code + ".message", args, locale);

        return (ApiResponse<T>) ApiResponse.builder()
                .status(status)
                .code(code)
                .message(translatedMessage)
                .data(data)
                .build();
    }
}