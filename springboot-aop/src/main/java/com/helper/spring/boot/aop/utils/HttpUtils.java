package com.helper.spring.boot.aop.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 大家都知道 能在Controller/action层获取 HttpServletRequest ，
 * 但是这里给大家备份的 是 从代码内部  service层获取HttpServletRequest 工具类
 * @author jaydon
 */
public class HttpUtils {


	public static HttpSession getSession() {
		HttpSession session = getHttpServletRequest().getSession();
		return session;
	}
	public static HttpSession getSessionDirect() {
		HttpSession session = (HttpSession) RequestContextHolder.getRequestAttributes().resolveReference(RequestAttributes.REFERENCE_SESSION);
		return session;
	}
	public static String getRequestURI() {
		return getHttpServletRequest().getRequestURI();
	}

	/**
	 * 尝试获取当前请求的HttpServletRequest实例
	 *
	 * @return HttpServletRequest
	 */
	public static HttpServletRequest getHttpServletRequest() {
		try {
			return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 尝试获取当前请求的HttpServletResponse实例
	 *
	 * @return HttpServletResponse
	 */
	public static HttpServletResponse getHttpServletResponse() {
		try {
			return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		} catch (Exception e) {
			return null;
		}
	}

	public static Map<String, String> getHeaders(HttpServletRequest request) {
		Map<String, String> map = new LinkedHashMap<>();
		Enumeration<String> enumeration = request.getHeaderNames();
		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();
			String value = request.getHeader(key);
			map.put(key, value);
		}


		return map;
	}
	public static Map<String, String> getParameterMap(HttpServletRequest request) {
		Enumeration<String> enumeration = request.getParameterNames();
		Map<String,String> parameterMap = new HashMap<String,String>();
		while (enumeration.hasMoreElements()){
			String parameter = enumeration.nextElement();
			parameterMap.put(parameter,request.getParameter(parameter));
		}
		return parameterMap;
	}

	/**
	 * 获取请求客户端的真实ip地址
	 *
	 * @param request 请求对象
	 * @return ip地址
	 */
	public static String getIpAddress(HttpServletRequest request) {

		// 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
		String ip = request.getHeader("X-Forwarded-For");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} else if (ip.length() > 15) {
			String[] ips = ip.split(",");
			for (int index = 0; index < ips.length; index++) {
				String strIp = (String) ips[index];
				if (!("unknown".equalsIgnoreCase(strIp))) {
					ip = strIp;
					break;
				}
			}
		}
		return ip;
	}

	/**
	 * 获取请求客户端的真实ip地址
	 *
	 * @param
	 * @return ip地址
	 */
	public static String getIpAddress() {
		// 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
		return getIpAddress(getHttpServletRequest());
	}
}