package com.helper.flyway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication()
public class FlyWayApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlyWayApplication.class,args);
    }
}