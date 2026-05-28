package com.paycoms.cp7.global.error;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import com.paycoms.cp7.global.common.ApiResponse;
import com.paycoms.cp7.global.util.MessageUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageUtils messageUtils;

    // 커스텀 예외 처리 (직접 만든 비즈니스 에러)
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Object> handleBusinessException(BusinessException e) {
        log.warn("Business Error: code={}, args={}", e.getErrorCode(), e.getArgs());
        // MessageUtils를 사용하여 에러 코드에 맞는 응답 생성
        ApiResponse<Object> response = messageUtils.createResponse(e.getErrorCode(), null, e.getArgs());
        return response;
    }

    // 파라미터 유효성 검사 실패 (@Valid 에러)
    @SuppressWarnings("null")
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("Validation Error: {}", errorMessage);

        if (!errorMessage.contains(".")) {
            // 메시지 코드 형식이 아닌 경우, 일반 에러로 처리
            ApiResponse<Object> response = messageUtils.createResponse("SYS_400", null);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        String[] err = errorMessage.split("\\.");

        ApiResponse<Object> response = messageUtils.createResponse(err[0], null, err[1]);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 파라미터 유효성 검사 실패 (@Valid 에러)
    @SuppressWarnings("null")
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(BindException e) {
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("Validation Error: {}", errorMessage);

        if (!errorMessage.contains(".")) {
            // 메시지 코드 형식이 아닌 경우, 일반 에러로 처리
            ApiResponse<Object> response = messageUtils.createResponse("SYS_400", null);
            return ResponseEntity.status(response.getStatus()).body(response);
        }
        String[] err = errorMessage.split("\\.");

        ApiResponse<Object> response = messageUtils.createResponse(err[0], null,
                err[1]);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoResourceFoundException(NoResourceFoundException e) {
        ApiResponse<Object> response = messageUtils.createResponse("SYS_404", null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("Method Not Allowed: {}", e.getMethod());
        ApiResponse<Object> response = messageUtils.createResponse("SYS_405", null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        log.warn("Unsupported Media Type: {}", e.getContentType());
        ApiResponse<Object> response = messageUtils.createResponse("SYS_415", null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ApiResponse<Object>> handleMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException e) {
        ApiResponse<Object> response = messageUtils.createResponse("SYS_406", null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("Malformed request body: {}", e.getMessage());
        ApiResponse<Object> response = messageUtils.createResponse("SYS_400", null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingRequestParam(MissingServletRequestParameterException e) {
        log.warn("Missing request parameter: {}", e.getParameterName());
        ApiResponse<Object> response = messageUtils.createResponse("SYS_400", null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("Type mismatch: param={}, value={}", e.getName(), e.getValue());
        ApiResponse<Object> response = messageUtils.createResponse("SYS_400", null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // 그 외 모든 예상치 못한 에러 (NPE 등)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAllException(Exception e) {
        log.error("Unexpected Error: ", e); // 스택 트레이스 전체 로그 기록

        ApiResponse<Object> response = messageUtils.createResponse("SYS_500", null);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}