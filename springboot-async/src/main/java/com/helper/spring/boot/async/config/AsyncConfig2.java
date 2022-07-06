package com.helper.spring.boot.async.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.lang.reflect.Method;

/**
 * @author jaydon
 * @see AsyncConfig
 * 不能同时配置两个: AsyncConfig 和 AsyncConfig2选一个
 */

@Slf4j
public class AsyncConfig2 implements AsyncConfigurer {
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        //重写代码
        return new AsyncUncaughtExceptionHandler() {
            @Override
            public void handleUncaughtException(Throwable ex, Method method, Object... objects) {
                log.error("AsyncConfig2");
                String name = ex.getClass().getName();
                //可替代异常处理
                if (name.contains("xxxException")) {
                    //xxx自定义异常处理
                }
            }
        };
    }

}

