package com.tr.auth.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Auth 异常处理
 *
 * @Author: TR
 */
@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(OAuth2Exception.class)
    public ResponseEntity handle(OAuth2Exception e) {
        log.error("[OAuth2Exception]：{}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity handle(AuthenticationException e) {
        log.error("[AuthenticationException]：{}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity handle(BadCredentialsException e) {
        log.error("[BadCredentialsException]：{}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity handle(AccessDeniedException e) {
        log.error("[AccessDeniedException]：{}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("没有操作权限");
    }

}
