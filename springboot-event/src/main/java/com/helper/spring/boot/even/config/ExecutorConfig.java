package com.helper.spring.boot.even.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author jaydon
 */
@Configuration
@Slf4j
public class ExecutorConfig {
    @Value("${async.executor.thread.core_pool_size:30}")
    private int corePoolSize;
    @Value("${async.executor.thread.max_pool_size:200}")
    private int maxPoolSize;
    @Value("${async.executor.thread.queue_capacity:100}")
    private int queueCapacity;
    @Value("${async.executor.thread.name.prefix:jaydon-async-service-}")
    private String namePrefix;
    @Bean("threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        log.info("start asyncServiceExecutor");
        log.info("corePoolSize={}",corePoolSize);
        log.info("maxPoolSize={}",maxPoolSize);
        log.info("queueCapacity={}",queueCapacity);
        log.info("namePrefix={}",namePrefix);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(corePoolSize);
        //配置最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        //配置队列大小
        executor.setQueueCapacity(queueCapacity);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix(namePrefix);
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }
}
