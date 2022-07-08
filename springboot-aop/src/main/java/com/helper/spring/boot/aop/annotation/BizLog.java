package com.helper.spring.boot.aop.annotation;

import com.helper.spring.boot.aop.eum.ApiRecord;

import java.lang.annotation.*;

/**
 * 描述业务 Biz 日志
 * @author jaydon
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BizLog {
	/**
	 * 描述API类型
	 */
	ApiRecord type() default ApiRecord.UNKNOWN;
	/**
	 * API描述
	 */
	String apiDesc() default "";

}
