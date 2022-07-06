package com.helper.spring.boot.even.listener4;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

/**
 * @author jaydon
 */
@Configuration
@Slf4j
public class WechatListener {
    @Async("threadPoolTaskExecutor")
    @EventListener(classes = {WechatNotifyEvent.class})
    public void listen(WechatNotifyEvent event) {
        log.info("jaydon,事件触发,事件={}，threadName = {}" ,
                event.getClass().getName(),Thread.currentThread().getName());
    }
}