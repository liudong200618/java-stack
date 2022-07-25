package com.helper.admin.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * 提供自定义健康信息
 * @author jaydon
 */
@Component
public class MyHealth implements HealthIndicator {
    @Override
    public Health health() {
        // perform some specific health check
        int errorCode = check();
        if (errorCode != 0) {
            return Health.down().withDetail("Error Code", errorCode).build();
        }
        return Health.up().build();

    /*  return new Health.Builder()
        .withDetail("tair", "timeout")
        .withDetail("tfs", "ok")
        .status("500")
        .down()
        .build();*/
    }

    private int check() {
        return 0;
    }
}