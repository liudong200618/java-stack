package com.helper.flyway.analyzer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * https://www.hxstrive.com/subject/spring_boot/360.htm
 *
 * FailureAnalyzer 是一种很好的方式在启动时拦截异常并将其转换为易读的消息，并将其包含在 FailureAnalysis 中。
 *
 * Spring Boot 为应用程序上下文相关异常、JSR-303 验证等提供了此类分析器。 实际上可以非常容易创建自己的 FailureAnalyzer。
 *
 * AbstractFailureAnalyzer 是 FailureAnalyzer 的一个抽象实现，它检查要处理的异常中是否存在指定的异常类型。
 *
 * 我们可以从中进行扩展，以便存在指定的异常时有机会去处理。如果由于某种原因无法处理异常，则返回 null 以使另一个实现有机会处理异常。
 *
 * FailureAnalyzer 实现将在 META-INF/spring.factories 中注册
 *
 * 自定义 FailureAnalyzer 对于监控应用启动过程十分有用。
 * 注意：自定义 FailureAnalyzer 处理不了应用启动 main 方法中的异常
 * @author jaydon
 */
@Slf4j
public class UnknownFailureAnalyzer extends AbstractFailureAnalyzer<Throwable> {
 
    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, Throwable cause) {
        log.error("rootFailure : \r\n",rootFailure);
        log.error("cause : \r\n",cause);
        return new FailureAnalysis(cause.getMessage(), "未知异常，请仔细检查", cause);
    }
 
}