package com.helper.spring.boot.even.listener6;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * spring.factories 中注册了
 * @author jaydon
 */
public class MyListener implements ApplicationListener {
 
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        System.out.println("MyListener onApplicationEvent()");
    }
}