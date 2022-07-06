package com.helper.spring.boot.even.listener;

import org.springframework.context.ApplicationEvent;

/**
 * @author jaydon
 */
public class UserReqEvent extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public UserReqEvent(Object source) {
        super(source);
    }
}
