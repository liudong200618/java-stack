package com.helper.spring.boot.even.listener5;

import com.helper.spring.boot.even.listener4.WechatNotifyEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

/**
 * @author jaydon
 */
@Configuration
@Slf4j
public class EventListenerCondition {

    /**
     * 只有 ConditionEven中的code为5的时候才通知
     * @param event 事件
     */
    @Async("threadPoolTaskExecutor")
    @EventListener(condition = "#event.code == 5")
    public void ConditionEven5(ConditionEven event) {
        log.info("ConditionEven5 = {}",event);
        log.info("事件触发,事件={}，threadName = {}" ,
                event.getClass().getName(),Thread.currentThread().getName());
    }

    /**
     * 只有 ConditionEven中的code为4的时候才通知
     * @param event 事件
     */
    @Async("threadPoolTaskExecutor")
    @EventListener(condition = "#event.code == 4")
    public void ConditionEven4(ConditionEven event) {
        log.info("ConditionEven4 = {}",event);
        log.info("事件触发,事件={}，threadName = {}" ,
                event.getClass().getName(),Thread.currentThread().getName());
    }
}
