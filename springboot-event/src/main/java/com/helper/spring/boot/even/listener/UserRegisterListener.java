package com.helper.spring.boot.even.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author jaydon
 */
@Slf4j
@Component
public class UserRegisterListener implements ApplicationListener<UserRegisterEvent> {

    /**
     *同步
     */
    @Override
    public void onApplicationEvent(UserRegisterEvent event) {
        log.info("同步,用户注册监听业务逻辑,threadName = {}",Thread.currentThread().getName());
    }
}
