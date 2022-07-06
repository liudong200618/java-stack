package com.helper.spring.boot.even.listener2;

import org.springframework.context.ApplicationEvent;

public class UserMailEvent extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public UserMailEvent(Object source) {
        super(source);
    }
}
