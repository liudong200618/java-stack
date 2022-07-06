package com.helper.spring.boot.even.listener2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author jaydon
 */
@Component
@Slf4j
public class UserMainListener {
    /**
     *异步,自定义线程池
     */
    @Async("threadPoolTaskExecutor")
    @EventListener
    public void onApplicationEvent(UserMailEvent event) {
        log.info("接收到邮件事件,threadName= {}" ,Thread.currentThread().getName());
    }
}
