package com.helper.spring.boot.even.listener;

import org.springframework.context.ApplicationEvent;

/**
 * @author jaydon
 */
public class UserRegisterEvent extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public UserRegisterEvent(Object source) {
        super(source);
    }
}
