package tw.fengqing.spring.springbucks.waiter.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 全域異常處理器
 * 用於處理應用程式中的各種異常，特別是驗證錯誤
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 處理驗證錯誤異常
     * 當 @Valid 註解驗證失敗時會被觸發
     * 
     * @param ex 驗證異常
     * @return 包含詳細錯誤資訊的響應
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        // 收集所有驗證錯誤
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        // 設定響應內容
        response.put("timestamp", java.time.LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("errors", errors);
        response.put("message", "Validation failed for object='" + 
                ex.getBindingResult().getObjectName() + "'. Error count: " + 
                ex.getBindingResult().getErrorCount());
        response.put("path", "/coffee/");
        
        log.warn("Validation failed: {}", errors);
        
        return ResponseEntity.badRequest().body(response);
    }
} 