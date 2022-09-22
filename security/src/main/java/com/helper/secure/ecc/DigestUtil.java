package com.helper.secure.ecc;

import cn.hutool.core.util.HexUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtil {

	public static String md5(String str) throws NoSuchAlgorithmException{
		return cn.hutool.core.util.HexUtil.encodeHexStr(md5(cn.hutool.core.util.HexUtil.decodeHex(str)));
	}

	public static byte[] md5(byte[] str) throws NoSuchAlgorithmException {
		return algorithm(str, "MD5");
	}

	public static String sha1(String str) throws NoSuchAlgorithmException{
		return cn.hutool.core.util.HexUtil.encodeHexStr(sha1(cn.hutool.core.util.HexUtil.decodeHex(str)));
	}
	public static byte[] sha1(byte[] str) throws NoSuchAlgorithmException {
		return algorithm(str, "SHA-1");
	}

	public static String sha256(String str) throws NoSuchAlgorithmException{
		System.out.println("明文 = " + str);
		return cn.hutool.core.util.HexUtil.encodeHexStr(sha256(cn.hutool.core.util.HexUtil.decodeHex(str)));
	}
	public static byte[] sha256(byte[] str) throws NoSuchAlgorithmException {
		return algorithm(str, "SHA-256");
	}
	
	
	public static byte[] algorithm(byte[] str, String algorithm) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(algorithm);
		md.update(str);
		return md.digest();
	}
	
	public static void main(String[] args) {
		try {
			String confirmationCode = "123456";
			String transactionId = "22223e9219d971b65658b360bdfed262";
			confirmationCode = HexUtil.encodeHexStr(DigestUtil.sha256(confirmationCode.getBytes()));
			System.out.println("第一次SHA："+confirmationCode);
			confirmationCode = DigestUtil.sha256(confirmationCode + transactionId);
			System.out.println("第二次SHA："+confirmationCode);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
}
