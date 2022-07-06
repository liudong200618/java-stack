package com.helper.spring.boot.even.listener4;

import org.springframework.context.ApplicationEvent;

public class WechatNotifyEvent extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public WechatNotifyEvent(Object source) {
        super(source);
    }
}
