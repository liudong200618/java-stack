package com.helper.spring.boot.even.listener3;

import org.springframework.context.ApplicationEvent;

public class TransactionalEvent  extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public TransactionalEvent(Object source) {
        super(source);
    }
}
