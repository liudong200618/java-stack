package com.helper.spring.boot.even.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * @author jaydon
 */
@Configuration
@Slf4j
public class EvenConfig {
    @EventListener(classes = {ApplicationEvent.class})
    public void listen(ApplicationEvent event) {
        log.info("事件触发：" + event.getClass().getName());
    }
}