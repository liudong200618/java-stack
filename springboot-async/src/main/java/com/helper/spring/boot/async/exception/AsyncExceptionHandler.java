package com.helper.spring.boot.async.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

/**
 * 异步的统一处理异常
 * @author jaydon
 */
@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    @Override
    public void handleUncaughtException(Throwable e, Method method, Object... objects) {
        log.error("异步线程发生异常了.");
        log.error("AsyncExceptionHandler,{}",e.getMessage(),e);
        log.error("method = {},args ={}",method.getName(),objects);
    }
}
