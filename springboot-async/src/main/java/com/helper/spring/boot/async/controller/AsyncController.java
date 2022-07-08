package com.helper.spring.boot.async.controller;

import com.helper.spring.boot.async.service.AsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author jaydon
 */
@RestController()
@RequestMapping("async")
@Slf4j
public class AsyncController {
    @Autowired
    private AsyncService asyncService;

    @PostMapping("async/local")
    public String async(){
        asyncService.async();
        log.info("同步线程,threadName = {}",Thread.currentThread().getName());
        return "同步返回数据";
    }

    @PostMapping("async/threadpool")
    public String asyncThreadPool(){
        asyncService.asyncThreadPool();
        log.info("自定义线程池,同步线程,threadName = {}",Thread.currentThread().getName());
        return "自定义线程池,同步返回数据";
    }

    @PostMapping("async/exception")
    public String asyncException(){
        asyncService.asyncException();
        log.info("自定义线程池,同步线程,threadName = {}",Thread.currentThread().getName());
        return "自定义线程池,同步返回数据";
    }
}
