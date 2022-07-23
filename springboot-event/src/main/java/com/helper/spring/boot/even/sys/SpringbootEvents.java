package com.helper.spring.boot.even.sys;

import com.helper.spring.boot.even.listener2.UserMailEvent;
import com.helper.spring.boot.even.listener3.TransactionalEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ExitCodeEvent;
import org.springframework.boot.autoconfigure.batch.JobExecutionEvent;
import org.springframework.boot.autoconfigure.jdbc.DataSourceSchemaCreatedEvent;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.builder.ParentContextApplicationContextInitializer;
import org.springframework.boot.context.event.*;
import org.springframework.boot.rsocket.context.RSocketServerInitializedEvent;
import org.springframework.boot.web.reactive.context.ReactiveWebServerInitializedEvent;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;

/**
 *
 * springboot内部自带事件
 * @author jaydon
 */
@Component
@Slf4j
public class SpringbootEvents {

    @EventListener
    public void onExitCodeEvent(ExitCodeEvent event) {
        log.info("###### {}" ,event);
    }

    @EventListener
    public void onParentContextAvailableEvent(ParentContextApplicationContextInitializer.ParentContextAvailableEvent event) {
        log.info("###### {}" ,event);
    }

    @EventListener
    public void onDataSourceSchemaCreatedEvent(DataSourceSchemaCreatedEvent event) {
        log.info("###### {}" ,event);
    }

    @EventListener
    public void onApplicationContextInitializedEvent(ApplicationContextInitializedEvent event) {
        log.info("###### {}" ,event);
    }

    @EventListener
    public void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
        log.info("###### {}" ,event);
    }

    @EventListener
    public void onApplicationPreparedEvent(ApplicationPreparedEvent event) {
        log.info("###### {}" ,event);
    }

    @EventListener
    public void onApplicationReadyEvent(ApplicationReadyEvent event) {
        log.info("###### {}" ,event);
    }

    @EventListener
    public void onApplicationStartedEvent(ApplicationStartedEvent event) {
        log.info("###### {}" ,event);
    }

    @EventListener
    public void onApplicationFailedEvent(ApplicationFailedEvent event) {
        log.info("###### {}" ,event);
    }

    @EventListener
    public void onApplicationStartingEvent(ApplicationStartingEvent event) {
        log.info("###### {}" ,event);
    }
    @EventListener
    public void onRSocketServerInitializedEvent(RSocketServerInitializedEvent event) {
        log.info("###### {}" ,event);
    }

    @EventListener
    public void onJobExecutionEvent(JobExecutionEvent event) {
        log.info("###### {}" ,event);
    }

    @EventListener
    public void onReactiveWebServerInitializedEvent(ReactiveWebServerInitializedEvent event) {
        log.info("###### {}" ,event);
    }

    @EventListener
    public void onServletWebServerInitializedEvent(ServletWebServerInitializedEvent event) {
        log.info("###### {}" ,event);
    }

    @EventListener
    public void onAvailabilityChangeEvent(AvailabilityChangeEvent event) {
        log.info("###### {}" ,event);
    }

    @EventListener
    public void onContextClosedEvent(ContextClosedEvent event) {
        log.info("###### {}" ,event);
    }

    @EventListener
    public void onContextRefreshedEvent(ContextRefreshedEvent event) {
        log.info("###### {}" ,event);
    }

    @EventListener
    public void onContextStoppedEvent(ContextStoppedEvent event) {
        log.info("###### {}" ,event);
    }

    @EventListener
    public void onContextStartedEvent(ContextStartedEvent event) {
        log.info("###### {}" ,event);
    }

    @EventListener
    public void onServletRequestHandledEvent(ServletRequestHandledEvent event) {
        log.info("###### {}" ,event);
    }

    @EventListener
    public void onTransactionalEvent(TransactionalEvent event) {
        log.info("###### {}" ,event);
    }

}
