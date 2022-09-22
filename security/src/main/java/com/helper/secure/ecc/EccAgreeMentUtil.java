package com.helper.secure.ecc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.security.NoSuchAlgorithmException;

public class EccAgreeMentUtil {
	private static Log log = LogFactory.getLog(EccAgreeMentUtil.class);

	public static String ecc_x963_kdf(String shs, String shareinfo, int outlen) throws NoSuchAlgorithmException{
		int hash_len = 32;
		double n = (double)outlen/hash_len;
		int j = (int) Math.ceil(n);
		
		String keydata = "";
		for (int i = 1; i <= j; i++){
			keydata += DigestUtil.sha256(shs + HexUtil.intToHex(i, 8) + shareinfo);
			System.out.println("keydata = " + keydata);
		}
		return keydata.substring(0, 2*outlen);
	}
	

}
