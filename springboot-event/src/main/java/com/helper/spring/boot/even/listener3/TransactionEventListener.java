package com.helper.spring.boot.even.listener3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 *
 * @TransactionalEventListener
 * @TransactionalEventListener 和 @EventListener差了一个 Transactional，这个事务表示的意思是，
 * 事件的发送时机可以和事务绑定。
 *
 * TransactionPhase.BEFORE_COMMIT 在提交前
 * TransactionPhase.AFTER_COMMIT  在提交后
 * TransactionPhase.AFTER_ROLLBACK  在回滚后
 * TransactionPhase.AFTER_COMPLETION  在事务完成后
 * 默认 TransactionPhase.AFTER_COMMIT。
 * 指定发布时机避免的情况就是，比如注册用户，包含了一些耗时的操作，而这些操作中有异步非阻塞的，
 * 当执行到了发布事件的方法时。用户可能还没有创建完成，此时如果事件发布了，在监听器那边执行时，可能获取用户失败。
 * 而如果在事务提交后执行，就不会出现这种情况。

 * 这个监听者可以选择在事务完成后才会被执行，事务执行失败就不会被执行
 * @author jaydon
 */

@Component
@Slf4j
public class TransactionEventListener {

    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCompleteScriptEvent(TransactionalEvent event) {
        log.info("监听到提交事务的事件,开始更新redis.threadName ={}",Thread.currentThread().getName());
    }
    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onBeforeCommitEvent(TransactionalEvent event) {
        log.info("监听到BEFORE_COMMIT事件.threadName ={}",Thread.currentThread().getName());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    public void onAfterCompletionEvent(TransactionalEvent event) {
        log.info("监听到AFTER_COMPLETION事件.threadName ={}",Thread.currentThread().getName());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onRollBackEvent(TransactionalEvent event) {
        log.info("监听到AFTER_ROLLBACK事件.threadName ={}",Thread.currentThread().getName());
    }


}
