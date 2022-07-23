package com.helper.flyway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitialConfig {

   // @Value("${application.name}")
    private String applicationName;

}