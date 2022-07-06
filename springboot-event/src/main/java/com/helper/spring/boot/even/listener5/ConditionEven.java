package com.helper.spring.boot.even.listener5;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * @author jaydon
 */
@Getter
@Setter
@ToString
public class ConditionEven extends ApplicationEvent {
    private String msg;
    private int code;
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public ConditionEven(Object source) {
        super(source);
    }
    public ConditionEven(Object source,String msg,int code) {
        super(source);
        this.msg = msg;
        this.code = code;
    }
}
