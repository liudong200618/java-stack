package com.helper.spring.boot.aop.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 同步,全局异常处理
 * @author jaydon
 */
@RestControllerAdvice
@Slf4j
public class SyncExceptionHandle {

    @ExceptionHandler(value = Exception.class)
    public String processException(Exception ex) {
        log.error(" 统一异常处理："+ex.getMessage(), ex);
        return "SyncExceptionHandle";
    }


}
