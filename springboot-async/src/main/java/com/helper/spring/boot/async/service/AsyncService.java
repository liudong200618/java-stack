package com.helper.spring.boot.async.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AsyncService {
    @Async
    public void async(){
        log.info("异步线程,threadName = {}",Thread.currentThread().getName());
    }

    @Async("threadPoolTaskExecutor")
    public void asyncThreadPool(){
        log.info("自定义线程池,异步线程,threadName = {}",Thread.currentThread().getName());
    }
    @Async("threadPoolTaskExecutor")
    public void asyncException(){
        log.info("自定义线程池,异步线程,threadName = {}",Thread.currentThread().getName());
        int i = 1/0;
        log.info("不会打印此行");
    }

}

