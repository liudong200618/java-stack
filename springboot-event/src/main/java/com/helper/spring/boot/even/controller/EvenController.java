package com.helper.spring.boot.even.controller;

import com.helper.spring.boot.even.listener.UserRegisterEvent;
import com.helper.spring.boot.even.listener.UserReqEvent;
import com.helper.spring.boot.even.listener.UserReqExceptionEvent;
import com.helper.spring.boot.even.listener.UserUnRegisterEvent;
import com.helper.spring.boot.even.listener2.UserMailEvent;
import com.helper.spring.boot.even.listener3.TransactionalEvent;
import com.helper.spring.boot.even.listener4.WechatNotifyEvent;
import com.helper.spring.boot.even.listener5.ConditionEven;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController()
@RequestMapping("even")
@Slf4j
public class EvenController {

    @Autowired
    private ApplicationContext context;

    @PostMapping("even/sync")
    public String even(){
        context.publishEvent(new UserRegisterEvent("用户注册事件"));
        log.info("同步线程,threadName = {}",Thread.currentThread().getName());
        return "同步返回数据";
    }

    @PostMapping("even/async/local")
    public String evenAsyncLocal(){
        context.publishEvent(new UserUnRegisterEvent("用户注销事件"));
        log.info("默认线程池,threadName = {}",Thread.currentThread().getName());
        return "默认线程池,同步返回数据";
    }

    @PostMapping("even/async/threadpool")
    public String evenAsyncThreadpool(){
        context.publishEvent(new UserReqEvent("用户请求事件"));
        log.info("自定义线程池,主线程,threadName = {}",Thread.currentThread().getName());
        return "自定义线程池,同步返回数据";
    }

    @PostMapping("even/async/exception")
    public String evenAsyncException(){
        context.publishEvent(new UserReqExceptionEvent("用户请求异常事件"));
        log.info("自定义线程池,主线程,threadName = {}",Thread.currentThread().getName());
        return "自定义线程池,异常,同步返回数据";
    }

    /**
     * 注解形式
     * @return
     */
    @PostMapping("even/async/mail")
    public String asyncAnno(){
        context.publishEvent(new UserMailEvent("用户发送邮件事件"));
        log.info("自定义线程池,主线程,threadName = {}",Thread.currentThread().getName());
        return "自定义线程池,注解,同步返回数据";
    }

    /**
     * 注解形式
     * @return
     */
    @PostMapping("even/async/transaction")
    public String evenAsyncTransaction(){
        context.publishEvent(new TransactionalEvent("事务提交"));
        log.info("自定义线程池,主线程,threadName = {}",Thread.currentThread().getName());
        return "自定义线程池,注解,同步返回数据";
    }

    @PostMapping("even/async/wechat")
    public String evenWechatNotifyEvent(){
        context.publishEvent(new WechatNotifyEvent("微信通知事件"));
        log.info("自定义线程池,主线程,threadName = {}",Thread.currentThread().getName());
        return "自定义线程池,注解,同步返回数据";
    }

    @GetMapping("even/async/condition/{code}")
    public String evenCondition(@PathVariable("code") int code){
        context.publishEvent(new ConditionEven("条件通知事件","条件通知事件",code));
        log.info("自定义线程池,主线程,threadName = {}",Thread.currentThread().getName());
        return "自定义线程池,注解,同步返回数据";
    }

}
