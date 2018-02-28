package com.yatang.xc.xcr.uitls;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

/**
 * RSA加密解密DES密钥
 *
 * @author Jocerly
 */
public class RSAHelper {
    /**
     * 生成RSA密钥对(默认密钥长度为1024)
     *
     * @return
     */
    public static KeyPair generateRSAKeyPair() {
        return generateRSAKeyPair(1024);
    }

    /**
     * 生成RSA密钥对
     *
     * @param keyLength 密钥长度，范围：512～2048
     * @return
     */
    public static KeyPair generateRSAKeyPair(int keyLength) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(keyLength);
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * Xml格式化
     *
     * @return
     */
    public static String encodePublicKeyToXml(PublicKey key) {
        if (!RSAPublicKey.class.isInstance(key)) {
            return null;
        }
        RSAPublicKey pubKey = (RSAPublicKey) key;
        StringBuilder sb = new StringBuilder();

        try {
            sb.append("<RSAKeyValue>");
            sb.append("<Modulus>").append(new String(Base64.encode(pubKey.getModulus().toByteArray()), "utf-8")).append("</Modulus>");
            sb.append("<Exponent>").append(new String(Base64.encode(pubKey.getPublicExponent().toByteArray()), "utf-8")).append("</Exponent>");
            sb.append("</RSAKeyValue>");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static PublicKey decodePublicKeyFromXml(String xml) {
        xml = xml.replaceAll("\r", "").replaceAll("\n", "");
        BigInteger modulus = new BigInteger(1, Base64.decode(getMiddleString(xml, "<Modulus>", "</Modulus>").getBytes()));
        BigInteger publicExponent = new BigInteger(1, Base64.decode(getMiddleString(xml, "<Exponent>", "</Exponent>").getBytes()));

        RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(modulus, publicExponent);
        KeyFactory keyf;
        try {
            keyf = KeyFactory.getInstance("RSA");
            return keyf.generatePublic(rsaPubKey);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getMiddleString(String xmlStr, String beginStr, String endStr) {
        String str = xmlStr;
        int beginIdx = str.indexOf(beginStr) + beginStr.length();
        int endIdx = str.indexOf(endStr);
        String str2 = str.substring(beginIdx, endIdx);
        //System.out.println(str2);
        return str2;
    }

    public static PrivateKey decodePrivateKeyFromXml(String xml) {
        xml = xml.replaceAll("\r", "").replaceAll("\n", "");
        BigInteger modulus = new BigInteger(1, Base64.decode(getMiddleString(xml, "<Modulus>", "</Modulus>").getBytes()));
        BigInteger publicExponent = new BigInteger(1, Base64.decode(getMiddleString(xml, "<Exponent>", "</Exponent>").getBytes()));
        BigInteger privateExponent = new BigInteger(1, Base64.decode(getMiddleString(xml, "<D>", "</D>").getBytes()));
        BigInteger primeP = new BigInteger(1, Base64.decode(getMiddleString(xml, "<P>", "</P>").getBytes()));
        BigInteger primeQ = new BigInteger(1, Base64.decode(getMiddleString(xml, "<Q>", "</Q>").getBytes()));
        BigInteger primeExponentP = new BigInteger(1, Base64.decode(getMiddleString(xml, "<DP>", "</DP>").getBytes()));
        BigInteger primeExponentQ = new BigInteger(1, Base64.decode(getMiddleString(xml, "<DQ>", "</DQ>").getBytes()));
        BigInteger crtCoefficient = new BigInteger(1, Base64.decode(getMiddleString(xml, "<InverseQ>", "</InverseQ>").getBytes()));

        RSAPrivateCrtKeySpec rsaPriKey = new RSAPrivateCrtKeySpec(modulus, publicExponent, privateExponent, primeP, primeQ, primeExponentP, primeExponentQ, crtCoefficient);

        KeyFactory keyf;
        try {
            keyf = KeyFactory.getInstance("RSA");
            return keyf.generatePrivate(rsaPriKey);
        } catch (Exception e) {
            return null;
        }
    }

    public static String encodePrivateKeyToXml(PrivateKey key) {
        if (!RSAPrivateCrtKey.class.isInstance(key)) {
            return null;
        }
        RSAPrivateCrtKey priKey = (RSAPrivateCrtKey) key;
        StringBuilder sb = new StringBuilder();

        try {
            sb.append("<RSAKeyValue>");
            sb.append("<Modulus>").append(Base64.encode(priKey.getModulus().toByteArray())).append("</Modulus>");
            sb.append("<Exponent>").append(Base64.encode(priKey.getPublicExponent().toByteArray())).append("</Exponent>");
            sb.append("<D>").append(Base64.encode(priKey.getPrivateExponent().toByteArray())).append("</D>");
            sb.append("<P>").append(Base64.encode(priKey.getPrimeP().toByteArray())).append("</P>");
            sb.append("<Q>").append(Base64.encode(priKey.getPrimeQ().toByteArray())).append("</Q>");
            sb.append("<DP>").append(Base64.encode(priKey.getPrimeExponentP().toByteArray())).append("</DP>");
            sb.append("<DQ>").append(Base64.encode(priKey.getPrimeExponentQ().toByteArray())).append("</DQ>");
            sb.append("<InverseQ>").append(Base64.encode(priKey.getCrtCoefficient().toByteArray())).append("</InverseQ>");
            sb.append("</RSAKeyValue>");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 用公钥加密私钥
     *
     * @param data
     * @param pubKey
     * @return
     */
    public static byte[] encryptData(byte[] data, PublicKey pubKey) {
        try {
            // 对数据加密  
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密  
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > 117) {
                    cache = cipher.doFinal(data, offSet, 117);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * 117;
            }
            byte[] encryptedData = out.toByteArray();
            out.close();
            return encryptedData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用私钥解密
     *
     * @param encryptedData
     * @param priKey
     * @return
     */
    public static byte[] decryptData(byte[] encryptedData, PrivateKey priKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            int inputLen = encryptedData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > 128) {
                    cache = cipher.doFinal(encryptedData, offSet, 128);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * 128;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
            return decryptedData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}