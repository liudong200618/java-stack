package com.helper.spring.boot.even.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author jaydon
 */
@Slf4j
@Component
public class AsyncPoolExpUserReqListener implements ApplicationListener<UserReqExceptionEvent> {

    /**
     *异步,自定义线程池
     */
    @Async("threadPoolTaskExecutor")
    @Override
    public void onApplicationEvent(UserReqExceptionEvent event) {
        log.info("异步,指定线程池,用户注册监听业务逻辑,threadName = {}",Thread.currentThread().getName());
        int i = 1/0;
    }
}
