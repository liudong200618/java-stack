package com.helper.secure.rsa;

import org.junit.Test;
import sun.misc.BASE64Encoder;
import sun.misc.IOUtils;

import javax.crypto.Cipher;
import java.security.*;

public class RsaKeyPair {



    @Test
    public  void gen2048() throws NoSuchAlgorithmException {
        // 获取指定算法的密钥对生成器
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");

        // 初始化密钥对生成器（指定密钥长度, 使用默认的安全随机数源）
        gen.initialize(2048);

        // 随机生成一对密钥（包含公钥和私钥）
        KeyPair keyPair = gen.generateKeyPair();

        // 获取 公钥 和 私钥
        PublicKey pubKey = keyPair.getPublic();
        PrivateKey priKey = keyPair.getPrivate();

        // 获取 公钥和私钥 的 编码格式（通过该 编码格式 可以反过来 生成公钥和私钥对象）
        byte[] pubEncBytes = pubKey.getEncoded();
        byte[] priEncBytes = priKey.getEncoded();

        // 把 公钥和私钥 的 编码格式 转换为 Base64文本 方便保存
        String pubEncBase64 = new BASE64Encoder().encode(pubEncBytes);
        String priEncBase64 = new BASE64Encoder().encode(priEncBytes);
        System.out.println("priEncBase64 = " + priEncBase64);
        System.out.println("pubEncBase64 = " + pubEncBase64);

        /* 通过该方法保存的密钥, 通用性较好, 使用其他编程语言也可以读取使用（推荐） */


    }
    @Test
    public  void gen1152() throws Exception {
        // 获取指定算法的密钥对生成器
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");

        // 初始化密钥对生成器（指定密钥长度, 使用默认的安全随机数源）
        gen.initialize(1152);

        // 随机生成一对密钥（包含公钥和私钥）
        KeyPair keyPair = gen.generateKeyPair();

        // 获取 公钥 和 私钥
        PublicKey pubKey = keyPair.getPublic();
        PrivateKey priKey = keyPair.getPrivate();

        // 获取 公钥和私钥 的 编码格式（通过该 编码格式 可以反过来 生成公钥和私钥对象）
        byte[] pubEncBytes = pubKey.getEncoded();
        byte[] priEncBytes = priKey.getEncoded();

        // 把 公钥和私钥 的 编码格式 转换为 Base64文本 方便保存
        String pubEncBase64 = new BASE64Encoder().encode(pubEncBytes);
        String priEncBase64 = new BASE64Encoder().encode(priEncBytes);
        System.out.println("priEncBase64 = " + priEncBase64);
        System.out.println("pubEncBase64 = " + pubEncBase64);

        /* 通过该方法保存的密钥, 通用性较好, 使用其他编程语言也可以读取使用（推荐） */

        // 原文数据
        String data = "你好, World!";

        // 客户端: 用公钥加密原文, 返回加密后的数据
        byte[] cipherData = encrypt(data.getBytes(), pubKey);

        // 服务端: 用私钥解密数据, 返回原文
        byte[] plainData = decrypt(cipherData, priKey);

        // 输出查看解密后的原文
        // 结果打印: 你好, World!
        System.out.println(new String(plainData));



    }

    /**
     * 公钥加密数据
     */
    private static byte[] encrypt(byte[] plainData, PublicKey pubKey) throws Exception {
        // 获取指定算法的密码器
        Cipher cipher = Cipher.getInstance("RSA");

        // 初始化密码器（公钥加密模型）
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);

        // 加密数据, 返回加密后的密文
        return cipher.doFinal(plainData);
    }

    /**
     * 私钥解密数据
     */
    private static byte[] decrypt(byte[] cipherData, PrivateKey priKey) throws Exception {
        // 获取指定算法的密码器
        Cipher cipher = Cipher.getInstance("RSA");

        // 初始化密码器（私钥解密模型）
        cipher.init(Cipher.DECRYPT_MODE, priKey);

        // 解密数据, 返回解密后的明文
        return cipher.doFinal(cipherData);
    }

}
