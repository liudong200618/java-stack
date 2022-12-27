package com.helper.spring.boot.even.Disruptor;

public interface DisruptorMqService {

    /**
     * 消息
     * @param message
     */
    void sayHelloMq(String message);
}