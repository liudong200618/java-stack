package com.helper.flyway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.text.SimpleDateFormat;
import java.util.Date;
 
@RestController
public class IndexController {
 
    public IndexController() {
       /* System.out.println("Init IndexController");
        SimpleDateFormat simpleDateFormat = null;
        if(System.currentTimeMillis() % 100 == 0) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        System.out.println("DATE::" + simpleDateFormat.format(new Date()));*/
    }
 
    @RequestMapping("/")
    public String index() {
        return "Spring Boot SpringApplication";
    }
}