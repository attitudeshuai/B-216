package com.dormitory.system.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "参数错误");
        String message = e.getMessage();
        if (message != null && message.contains("The given id must not be null")) {
            message = "操作ID不能为空";
        }
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(org.springframework.dao.EmptyResultDataAccessException.class)
    public ResponseEntity<Map<String, String>> handleEmptyResultDataAccessException(org.springframework.dao.EmptyResultDataAccessException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "操作失败");
        response.put("message", "记录不存在或已被删除");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "操作失败");
        String message = e.getMessage();
        if (message != null) {
            if (message.contains("The given id must not be null")) {
                message = "操作ID不能为空";
            } else if (message.contains("nested exception is java.lang.IllegalArgumentException")) {
                message = "参数错误";
            }
        }
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "系统错误");
        response.put("message", "服务器内部错误，请联系管理员");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
