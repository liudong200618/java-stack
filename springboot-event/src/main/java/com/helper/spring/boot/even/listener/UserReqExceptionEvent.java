package com.helper.spring.boot.even.listener;

import org.springframework.context.ApplicationEvent;

public class UserReqExceptionEvent extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public UserReqExceptionEvent(Object source) {
        super(source);
    }
}
