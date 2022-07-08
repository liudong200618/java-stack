package com.helper.spring.boot.aop.eum;


public enum ApiRecord {
		/** 获取订单详情 */
		ORDER_DETAIL(1, "订单详情"),
		/** 未知 */
		UNKNOWN(2, "其它");
		
		public int code;
		public String message;

		ApiRecord(int code, String message) {
			this.code = code;
			this.message = message;
		}
	}