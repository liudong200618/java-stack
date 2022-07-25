package com.helper.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *Spring Boot Admin 是一个针对 Spring Boot 的 Actuator 接口进行 UI 美化封装的监控工具
 * https://developer.aliyun.com/article/826593
 *
 * @author jaydon
 *
 * http://localhost:7077/
 */
// 启用Admin服务器
@EnableAdminServer
@SpringBootApplication
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class,args);
    }
}
