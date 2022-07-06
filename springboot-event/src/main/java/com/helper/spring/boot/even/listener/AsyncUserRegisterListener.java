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
public class AsyncUserRegisterListener implements ApplicationListener<UserUnRegisterEvent> {

    /**
     *异步,默认线程池
     */
    @Async()
    @Override
    public void onApplicationEvent(UserUnRegisterEvent event) {
        log.info("异步,默认线程池,用户注销监听业务逻辑,threadName = {}",Thread.currentThread().getName());
    }
}
