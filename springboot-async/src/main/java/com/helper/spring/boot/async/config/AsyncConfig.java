package com.helper.spring.boot.async.config;


import com.helper.spring.boot.async.exception.AsyncExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

/**
 * 异步的统一处理异常
 * @author jaydon
 */
@Configuration
@Slf4j
public class AsyncConfig implements AsyncConfigurer {
    @Bean
    public AsyncExceptionHandler asyncExceptionHandler(){
        return new AsyncExceptionHandler();
    }
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return asyncExceptionHandler();
    }

}
