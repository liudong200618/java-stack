package com.helper.spring.boot.aop.controller;

import com.helper.spring.boot.aop.annotation.BizLog;
import com.helper.spring.boot.aop.eum.ApiRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jaydon
 */
@RestController
@RequestMapping("aop")
@Slf4j
public class AopController {

    @BizLog(apiDesc = "获取订单详情",type = ApiRecord.ORDER_DETAIL)
    @PostMapping("order/detail")
    public String orderDetail(@RequestBody String orderDetail){
        log.info("orderDetail = {}",orderDetail);
        return orderDetail;
    }

}
