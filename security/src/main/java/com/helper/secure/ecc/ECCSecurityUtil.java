package com.helper.secure.ecc;



/*import com.jeasy.kits.ByteBuffer;
import com.tmc.tsm.biz.api.model.uitl.EccAgreeMentUtil;*/
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.NullCipher;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;


public class ECCSecurityUtil {

    private static Log log = LogFactory.getLog(ECCSecurityUtil.class);

	/** 指定加密算法为EC */
    private static final String ALGORITHM = "EC";
    /** 指定签名算法*/
    static final String ALGORITHM_SIGN = "SHA256withECDSA";  
    /** 公钥前相同字符串*/
    private static final String publicKeyFront = "3059301306072A8648CE3D020106082A8648CE3D030107034200";
    /** 私钥前相同字符串*/
    private static final String privateKeyFront = "3041020100301306072A8648CE3D020106082A8648CE3D030107042730250201010420";

    
    public static final String NISTP256 = "prime256v1";
    /**
     * 生成密钥对
     * @throws Exception
     */
   public static Map<String, String> generateKeyPair(String ecparameters) throws Exception {
	 KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);  
     keyPairGen.initialize(256);   // 初始化密钥对生成器，密钥大小为256位  
     KeyPair keyPair = keyPairGen.generateKeyPair();  
     ECPublicKey eccPublicKey = (ECPublicKey) keyPair.getPublic();  
     byte[] keyBs = eccPublicKey.getEncoded();  ////返回基本编码格式的密钥,如果此密钥不支持编码,则返回 null。
//     eccPublicKey.getParams().getCurve().
     String publicKeyString =  HexUtil.encodeHexStr(keyBs);
     log.debug("生成的公钥：\r\n" + publicKeyString.replace(publicKeyFront, ""));
     ECPrivateKey eccPrivateKey = (ECPrivateKey) keyPair.getPrivate();  
     keyBs = eccPrivateKey.getEncoded();  
     String privateKeyString = HexUtil.encodeHexStr(keyBs);
     log.debug("生成的私钥：\r\n" + privateKeyString.replace(privateKeyFront, ""));
     
     Map<String, String> keyPairMap = new HashMap<String, String>();
     keyPairMap.put("publicKey", publicKeyString.replace(publicKeyFront, ""));
     keyPairMap.put("privateKey", privateKeyString.replace(privateKeyFront, ""));
     return keyPairMap;
   }

    /**
     * 获取公钥 
     * @return 
     * @throws Exception 
     */
    static ECPublicKey getPublicKey(String publicKey) throws Exception {  
    	X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(HexUtil.decodeHex(publicKeyFront+publicKey));  
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);  
        return (ECPublicKey)keyFactory.generatePublic(publicKeySpec);
    }

    /**
     * 获取公钥
     * @return
     * @throws Exception
     */
    static ECPublicKey getPublicKeyX509Encoded(String publicKeyX509Encoded) throws Exception {
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(HexUtil.decodeHex(publicKeyX509Encoded));
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return (ECPublicKey)keyFactory.generatePublic(publicKeySpec);
    }

    /**
     * 获取私钥 
     * @return 
     * @throws Exception 
     */
    static ECPrivateKey getPrivateKey(String privateKey) throws Exception {  
    	PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(HexUtil.decodeHex(privateKeyFront+privateKey));  
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);  
        return (ECPrivateKey)keyFactory.generatePrivate(privateKeySpec); 
    }  
    
    /**
     * 公钥加密 
     * @param
     * @return 
     * @throws Exception 
     */
    static String encryptionByPublicKey(String source, String publicKeyString) throws Exception{  
    	ECPublicKey publicKey = getPublicKey(publicKeyString); 
        ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(publicKey.getW(), publicKey.getParams());
        Cipher cipher = new NullCipher();
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, ecPublicKeySpec.getParams()); 
        byte[] output = cipher.doFinal(HexUtil.decodeHex(source)); 
        String target = HexUtil.encodeHexStr(output);
        log.debug("公钥加密后的数据：\r\n" + target);
        return target;  
    }  
    
    /**
     * 公钥解密 
     * @param target 
     * @throws Exception 
     */
    static void decryptionByPublicKey(String target, String publicKeyString) throws Exception{  
    	ECPublicKey publicKey = getPublicKey(publicKeyString);  
    	ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(publicKey.getW(), publicKey.getParams());
        Cipher cipher = new NullCipher(); 
        cipher.init(Cipher.DECRYPT_MODE, publicKey, ecPublicKeySpec.getParams());
        byte[] output = cipher.doFinal(HexUtil.decodeHex(target));
        String source = HexUtil.encodeHexStr(output);;
        log.debug("公钥解密后的数据：\r\n" + source);
    }  

    
    /**
     * 公钥验证签名 
     * @return 
     * @throws Exception 
     */
    public static boolean verifyByPublicKey(String ecparameters,String target, String publicKeyString, String sign) throws Exception{  
    	ECPublicKey publicKey = getPublicKey(publicKeyString);  
        Signature signature = Signature.getInstance(ALGORITHM_SIGN);  
        signature.initVerify(publicKey);  
        signature.update(HexUtil.decodeHex(target));  
        if (signature.verify(HexUtil.decodeHex(sign))) {
           // log.info("签名正确！");
            return true;
        } else {
            log.info("签名错误！");
            return false;
        }  
    }

    /**
     * 公钥验证签名
     * @return
     * @throws Exception
     */
    public static boolean verifyByPublicKeyX509Encoded(String target, String publicKeyString, String sign) throws Exception{
        ECPublicKey publicKey = getPublicKeyX509Encoded(publicKeyString);
        Signature signature = Signature.getInstance(ALGORITHM_SIGN);
        signature.initVerify(publicKey);
        signature.update(HexUtil.decodeHex(target));
        if (signature.verify(HexUtil.decodeHex(sign))) {
            //log.info("签名正确！");
            return true;
        } else {
            log.info("签名错误！");
            return false;
        }
    }



    // r || s , 如果 s 以 00开头，先 ans.1转换后才能验签通过
    /**
     * 公钥验证签名
     * @return
     * @throws Exception
     */
    public static boolean verifyByPublicKeyX509EncodedUseBcLib(String target, String publicKeyString, String sign) throws Exception{
        ECPublicKey publicKey = getPublicKeyX509Encoded(publicKeyString);
        Signature signature = Signature.getInstance(ALGORITHM_SIGN,new BouncyCastleProvider());
        signature.initVerify(publicKey);
        signature.update(HexUtil.decodeHex(target));
        if (signature.verify(HexUtil.decodeHex(sign))) {
          //  log.info("签名正确！");
            return true;
        } else {
            log.info("签名错误！");
            return false;
        }
    }

   /* public static boolean verifyByPublicKeyX509EncodedUseBcLibAsn1(String target, String publicKeyString, byte[] signature) throws Exception{
        ECPublicKey publicKey = getPublicKeyX509Encoded(publicKeyString);

        int blockSize = publicKey.getParams().getCurve().getField().getFieldSize() / 8;
        if (signature.length != blockSize * 2) {
            throw new ArithmeticException("The length of signature should be equals to elliptic field size of curve: " + blockSize);
        }

        ByteBuffer buffer = new ByteBuffer(signature);
        int length = signature.length / 2;
        BigInteger r = new BigInteger(1, buffer.removeByteArray(length));
        BigInteger s = new BigInteger(1, buffer.removeByteArray(length));
        ASN1Integer[] array = new ASN1Integer[]{new ASN1Integer(r), new ASN1Integer(s)};
        DLSequence seq = new DLSequence(array);
        signature = seq.getEncoded();

        Signature signaturer = Signature.getInstance(ALGORITHM_SIGN,new BouncyCastleProvider());
        signaturer.initVerify(publicKey);
        signaturer.update(HexUtil.decodeHex(target));
        if (signaturer.verify(signature)) {
          //  log.info("签名正确！");
            return true;
        } else {
            log.info("签名错误！");
            return false;
        }
    }*/
    
    /**
     * 私钥解密 
     * @param target 
     * @throws Exception 
     */
    static void decryptionByPrivateKey(String target, String privateKeyString) throws Exception {  
    	ECPrivateKey privateKey = getPrivateKey(privateKeyString);
    	ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(privateKey.getS(), privateKey.getParams());
        Cipher cipher = new NullCipher(); 
        cipher.init(Cipher.DECRYPT_MODE, privateKey, ecPrivateKeySpec.getParams());
        byte[] output = cipher.doFinal(HexUtil.decodeHex(target));
        String source = HexUtil.encodeHexStr(output);
        log.debug("私钥解密后的数据：\r\n" + source);
    }  
    
    /**
     * 私钥加密 
     * @param
     * @return 
     * @throws Exception 
     */
    static String encryptionByPrivateKey(String source, String privateKeyString) throws Exception {  
    	ECPrivateKey privateKey = getPrivateKey(privateKeyString);  
        Cipher cipher = new NullCipher();
        ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(privateKey.getS(), privateKey.getParams());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey, ecPrivateKeySpec.getParams()); 
        byte[] output = cipher.doFinal(HexUtil.decodeHex(source));
        String target = HexUtil.encodeHexStr(output);
        log.debug("私钥加密后的数据：\r\n" + target);
        return target;  
    }  
    
    /**
     * 私钥签名 
     * @param target 
     * @return 
     * @throws Exception 
     */
    public static String signByPrivateKey(String ecparameters,String target, String privateKeyString) throws Exception{  
    	ECPrivateKey privateKey = getPrivateKey(privateKeyString);  
        Signature signature = Signature.getInstance(ALGORITHM_SIGN);  
        signature.initSign(privateKey);  
        signature.update(HexUtil.decodeHex(target));  
        String sign = HexUtil.encodeHexStr(signature.sign());
        log.debug("生成的签名：\r\n" + sign);
        return sign;  
    }

    public static void main(String[] args) throws Exception {
        //  由cos提供
         String ePK_AP_ECKA = "04708FFE66B49E37C3722161526C8698A6A18BC86537999C12F4C157027EC2C493730030226518B22396E974B45B0A004743C0C14455B568673DAAAEAE2EBA183E";
         String eSK_AP_ECKA = "D7C95B4C65FD5F005AACAA6F4106A94D25CEB9DB6B17AFD6C02E8CBFAFC7A1E4";

        String source = "3A0217A6159002030995011080018881011082010183013091007F494104805A689368C520BB1184DD59BB2A2B3CA37EF0CFF0DE2D74578AD4CA7DB28E121DD8152514807955B51CA9F904947AD51C9A1340B48BD6D5E941CFFDA04D6EB50085108EB1A819A4AFA98D8AEE11C6D2D77CA5";

    /*    String publicKeyPointQServerTmp1 = "048D45FA538F79DAFCE37D213875FD1918A9B27E5E02D288596D611032147267A21CA93D2EDD4FE4845F1DC7977C28312163189929BC70A1C07BD825BC6DB24749";
        String    eckaCasdPrivateKeyServerTmp1 = "625348772A8AA9F20E41986CB2A3925835DCD98773F085DEBCC447A4C9367516";
        String     publicKeyPointQSETmp1 = "04EC8CB34F2714236F3E22C74BBA425B0C9FD26C4DA8207A0D6459D9060ED0E57E2E975BA8161559633D3C83EC26071439505258591C9CD7793A991A06E6A20B18";
        String    eckaCasdPrivateKeySETmp1 = "C3C696C6847FDC9B1F7773BE7A0A89B137D2FE63ADD20B2E8A63281CBDDC0C97";*/


//        String   publicKeyPointQServer = "0469169ADD1EDA8B3DD2EF00E841FA831B2FC9ACCAD46C1EA19B4EB1031AD653E65D47ABBCE5C678199EE32857B60F34DC08FD26C3EF69EADC04F97DF324CE2E6A";
//        String    eckaCasdPrivateKeyServer = "03FE9F49A1F817B1D4124EB075DD1F78A05F77CEC0C39B615CA82D3D9FFADDA5";
//
//
//        String   publicKeyPointQSE = "04D7298827B3D115EFFCB6B591AD6EEBF969F3A9ED635FD5D4B9103870764C4B1EBE2E75D73292A6510939450BE16E296F07537F06EB93A3C8B3B39F91C6AD8182";
//        String  eckaCasdPrivateKeySE = "87ABD4D9EBCD68757186BB52564C93B2F886C3201D08C5CD553A0A81B64AC5A9";



        String eckaPrivateKey = "3D8F79DFB9C79BEB85DFA965BFCDA91ED559DA0F85BE6ACAC63ED55C0D2D515E";

//        String shareinfo = iscr.getControlRefTemplate().getKeyType() +
//                HexUtil.intToHex(iscr.getControlRefTemplate().getKeyLen(), 2) +
//                StrHandleUtil.genBER_LV(iscr.getControlRefTemplate().getHostId()) +
//                StrHandleUtil.genBER_LV(this.rspSession.getEid());


      /*
        Key usage qualifier 1 Mandatory
        Key type 1 Mandatory
        Key length 1 Mandatory
        Derivation random (DR) 16, 24, or 32 Conditional (see Note 1)
        HostID-LV Variable Conditional (see Note 2)
        IIN-LV Variable Conditional (see Note 2)
        CIN-LV Variable Conditional (see Note 2)
      */

  /*      Key Usage Qualifier
        '5C' (1 secure channel base key) or
        '10' (3 secure channel keys)
        (see [GPCS] Table 11-17)*/
        //SE

        //SEND 84E208001D 00A6 12 A6 10 90020300 950110 800180 810110 830122 EF0325673F01F267 SW9000

//        String combineSe = combine(eckaCasdPrivateKeySETmp1, publicKeyPointQServerTmp1);
//        System.out.println("combineSe = " + combineSe);

        // 不是 LV结构
        // 95 0110
        String KeyUsageQualifierLv = "10";
        // 80 0180
        String keyTypeLv = "80";
        // 81 0110
        String keyLengthLv = "10";

        String shareinfo = KeyUsageQualifierLv + keyTypeLv + keyLengthLv;
        System.out.println("shareinfo = " + shareinfo);
        //SERVER
        String shs = combine(eckaPrivateKey, ePK_AP_ECKA);
        System.out.println("公钥和私钥处理后的数据 shs = " + shs);

//        Assert.assertEquals(combineSe,shs);

        // 48
        String keyData = EccAgreeMentUtil.ecc_x963_kdf(shs, shareinfo, 64);
        String receiptKey = keyData.substring(0,32);
        String sENC = keyData.substring(32,64);
        String sMAC = keyData.substring(64,96);
        String sDek = keyData.substring(96);
        System.out.println("keyData = " + keyData);
        System.out.println("receiptKey = " + receiptKey);
        System.out.println("sENC = " + sENC);
        System.out.println("sMAC = " + sMAC);
        System.out.println("sDek = " + sDek);

    }
    
    /**
     * 计算共享密钥
     * @param privateKeyString
     * @param publicKeyString
     * @return
     * @throws Exception
     */
    public static String combine(String privateKeyString, String publicKeyString) throws Exception  {
    	PrivateKey privateKey = getPrivateKey(privateKeyString);
    	PublicKey publicKey = getPublicKey(publicKeyString);
		KeyAgreement ka = KeyAgreement.getInstance("ECDH");
		ka.init(privateKey);
		ka.doPhase(publicKey, true);
		SecretKey secretKey = ka.generateSecret("TlsPremasterSecret");
		byte[] keyBs = secretKey.getEncoded();  
	    String secretKeyString = HexUtil.encodeHexStr(keyBs);  
		return secretKeyString;
	}
    
	public static void main44(String[] args) throws Exception {

        String ePK_AP_ECKA = "04708FFE66B49E37C3722161526C8698A6A18BC86537999C12F4C157027EC2C493730030226518B22396E974B45B0A004743C0C14455B568673DAAAEAE2EBA183E";
        String eSK_AP_ECKA = "D7C95B4C65FD5F005AACAA6F4106A94D25CEB9DB6B17AFD6C02E8CBFAFC7A1E4";
    	// 生成公钥和私钥  
		Map<String, String> keyPairMap = generateKeyPair(""); 
    	String publicKeyString = "04805A689368C520BB1184DD59BB2A2B3CA37EF0CFF0DE2D74578AD4CA7DB28E121DD8152514807955B51CA9F904947AD51C9A1340B48BD6D5E941CFFDA04D6EB5";
    	String privateKeyString = "ED8B1679723E7B02CCFEC0980563C1982ECB858F996EA28E8F626F490AABC997";
    	
        String source = "3A0217A6159002030995011080018881011082010183013091007F494104805A689368C520BB1184DD59BB2A2B3CA37EF0CFF0DE2D74578AD4CA7DB28E121DD8152514807955B51CA9F904947AD51C9A1340B48BD6D5E941CFFDA04D6EB50085108EB1A819A4AFA98D8AEE11C6D2D77CA5";  
       
        //System.out.println("--------------------------公钥加密，私钥解密------------------------------");  
        //System.out.println("加密前的数据：\r\n" + source);  
        // 公钥加密  
        //String target = encryptionByPublicKey(source, publicKeyString);  
        // 私钥解密  
        //decryptionByPrivateKey(target, privateKeyString);  
          
       /* System.out.println("--------------------------私钥签名，公钥验证签名------------------------------");
        String source = "3A0217A6159002030995011080018881011082010183013091007F494104805A689368C520BB1184DD59BB2A2B3CA37EF0CFF0DE2D74578AD4CA7DB28E121DD8152514807955B51CA9F904947AD51C9A1340B48BD6D5E941CFFDA04D6EB50085108EB1A819A4AFA98D8AEE11C6D2D77CA5";
        String privateKeyString = "584DC7E7F617F81028507F8784CE1D8C80C9A0B83721BFE4222293359E5292A5";
        String publicKeyString = "046126FF068E7ED6FC7B034F31A6F6ACE89D2CFF47258E755B11A11DCCA255F51C164F0C174A9350D2314713A4AF5DCDB86665EA09930C0CB923B22B5FB97377A7";
        // 签名  
        String sign = signByPrivateKey(source, privateKeyString);  
        System.out.println("签名后的数据：\r\n" + sign);
        // 验证签名  
        verifyByPublicKey(source, publicKeyString, sign); */
        
//		System.out.println("--------------------------A的私钥与B的公钥生成共享密钥------------------------------");
//    	String SM_EPK_ECKA="04805A689368C520BB1184DD59BB2A2B3CA37EF0CFF0DE2D74578AD4CA7DB28E121DD8152514807955B51CA9F904947AD51C9A1340B48BD6D5E941CFFDA04D6EB5";
//    	String SM_ESK_ECKA="ED8B1679723E7B02CCFEC0980563C1982ECB858F996EA28E8F626F490AABC997";
//        String PK_ECASD_ECKA = "046126FF068E7ED6FC7B034F31A6F6ACE89D2CFF47258E755B11A11DCCA255F51C164F0C174A9350D2314713A4AF5DCDB86665EA09930C0CB923B22B5FB97377A7";
//      String SK_ECASD_ECKA = "584DC7E7F617F81028507F8784CE1D8C80C9A0B83721BFE4222293359E5292A5";
        
//        String shareSecret1 = combine(SM_ESK_ECKA, PK_ECASD_ECKA);
//        System.out.println("共享密钥1："+shareSecret1);
       // String shareSecret2 = combine(cardPrivateKey, SM_EPK_ECKA);
        //System.out.println("共享密钥2："+shareSecret2);

        System.out.println("--------------------------私钥加密并且签名，公钥验证签名并且解密------------------------------");
        // 私钥加密  
        //target = encryptionByPrivateKey(source, privateKeyString);  
        // 签名  
        String sign = signByPrivateKey("",source, eSK_AP_ECKA);
        System.out.println("sign = " + sign);

        // 验证签名  
        verifyByPublicKey("",source, ePK_AP_ECKA, sign);
        // 公钥解密  
        //decryptionByPublicKey(target, publicKeyString); 
        
  
    }
	
}
