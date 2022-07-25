package com.helper.admin.health;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

/**
 * @author jaydon
 *
 * 自定义 Health Indicator
 * 当然你也可以自定义一个Health Indicator，只需要实现 HealthIndicator 接口或者继承 AbstractHealthIndicator类。
 *
 */
@Component
public class CustomHealthIndicator extends AbstractHealthIndicator {

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        // 使用 builder 来创建健康状态信息
        // 如果你throw 了一个 exception，那么status 就会被置为DOWN，异常信息会被记录下来
        builder.up()
                .withDetail("app", "这个项目很健康")
                .withDetail("error", "Nothing, I'm very good");
    }
}