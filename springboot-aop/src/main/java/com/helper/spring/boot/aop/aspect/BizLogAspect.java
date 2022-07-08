package com.helper.spring.boot.aop.aspect;


import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSON;
import com.helper.spring.boot.aop.annotation.BizLog;
import com.helper.spring.boot.aop.utils.HttpUtils;
import com.helper.spring.boot.aop.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


@Aspect
@Component
@Slf4j
public class BizLogAspect {


	@Pointcut("@annotation(com.helper.spring.boot.aop.annotation.BizLog)")
	public void pointCut() {
	
	}
	
	@Before("pointCut()")
	public void before(JoinPoint joinPoint)  {
		log.info("==> json方法调用开始...");
		//获取目标方法的参数信息
		Object[] obj = joinPoint.getArgs();
		log.info("获取目标方法的参数信息 = {}",obj);
		//AOP代理类的信息
		Object aThis = joinPoint.getThis();
		log.info("AOP代理类的信息 = {}",aThis);
		//代理的目标对象
		Object target = joinPoint.getTarget();
		log.info("代理的目标对象 = {}",target);
		//用的最多 通知的签名
		Signature signature = joinPoint.getSignature();
		//代理的是哪一个方法
		log.info("==> 代理的是哪一个方法 :"+signature.getName());
		//AOP代理类的名字
		log.info("==> AOP代理类的名字:"+signature.getDeclaringTypeName());
		//AOP代理类的类（class）信息
		Class declaringType = signature.getDeclaringType();
		log.info("AOP代理类的类（class）信息 = {}",declaringType);
		//获取RequestAttributes
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		//从获取RequestAttributes中获取HttpServletRequest的信息
		HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
		log.info("==> 请求者的IP："+request.getRemoteAddr());
		boolean getMethod = ServletUtil.isGetMethod(request);
		log.info("getMethod= {}",getMethod);

		HttpServletResponse httpServletResponse = HttpUtils.getHttpServletResponse();
		log.info("httpServletResponse = {}",httpServletResponse);

		String clientIP = ServletUtil.getClientIP(request);
		log.info("clientIP= {}",clientIP);

		String remoteRealIP = WebUtils.getRemoteRealIP(request);
		log.info("remoteRealIP= {}",remoteRealIP);

		log.info("HttpServletRequest = {}",request);
		//如果要获取Session信息的话，可以这样写：
		HttpSession session = HttpUtils.getSessionDirect();
		log.info("HttpSession = {}",session);

		String requestURI = HttpUtils.getRequestURI();
		log.info("requestURI = {}",requestURI);

		Map<String,String> parameterMap = HttpUtils.getParameterMap(request);
		String str = JSON.toJSONString(parameterMap);
		if(obj.length > 0) {
			log.info("==> 请求的参数信息为："+str);
		}

		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		Method method    = methodSignature.getMethod();
		// 获取方法的 @BigLog 注解，并拿到其中的值
		BizLog annotation = method.getAnnotation(BizLog.class);
		log.info("annotation = {}",annotation.apiDesc());

	}
	
	@AfterReturning(value = "pointCut()", returning = "respond")
	public void afterReturning(JoinPoint joinPoint,  Object respond) {
		log.info("返回值 = {}",respond);
	}

	/**
	 * AOP的AfterThrowing处理虽然可以对目标方法的异常进行处理，但这种处理与直接使用catch捕捉不同，catch捕捉意味着完全处理该异常，
	 * 如果catch块中没有重新抛出新的异常，则该方法可能正常结束；
	 * 而AfterThrowing处理虽然处理了该异常，但它不能完全处理异常，
	 * 该异常依然会传播到上一级调用者，即JVM
	 *
	 * controller发生的异常如果没有捕获会先进入该函数,再在统一异常处理中捕获并处理
	 * @param joinPoint
	 * @param t
	 */
	@AfterThrowing(pointcut = "pointCut()", throwing = "t")
	public void afterException(JoinPoint joinPoint, Throwable t) {
      log.info("afterException = {}",t);
	}

	@Around("pointCut()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		//获取方法参数值数组
		Object[] args = joinPoint.getArgs();
		//得到其方法签名
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		//获取方法参数类型数组
		Class[] paramTypeArray = methodSignature.getParameterTypes();

		args[0] = "更改后的参数";
		log.info("around,请求参数为{}",args);
		//动态修改其参数
		//注意，如果调用joinPoint.proceed()方法，则修改的参数值不会生效，必须调用joinPoint.proceed(Object[] args)
		Object result = joinPoint.proceed(args);
		log.info("around,响应结果为{}",result);
		//如果这里不返回result，则目标对象实际返回值会被置为null
		return result;
	}

	
}
