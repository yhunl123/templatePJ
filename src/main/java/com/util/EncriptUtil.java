package com.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;

public class EncriptUtil {
    @Value("${aes128.secretKey}")
    private String secretKey;
    @Value("${aes128.iv}")
    private String iv;

    /**
     * AES128 암호화
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String AesEncrypt(String str) throws UnsupportedEncodingException,
            NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException{
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        byte[] planText = str.getBytes("UTF-8");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
        SecretKeySpec newKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);

        return new String(org.codehaus.plexus.util.Base64.encodeBase64(cipher.doFinal(planText)),"UTF-8");
    }

    /**
     * AES128 복호화
     * @param str
     * @return String
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String AesDecrypt(String str) throws UnsupportedEncodingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        byte[] cipherText = org.codehaus.plexus.util.Base64.decodeBase64(str.getBytes("UTF-8"));
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
        SecretKeySpec newKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);

        return new String(cipher.doFinal(cipherText), "UTF-8");
    }


    /**
     * 단방향 암호화 (SHA256)
     * @param strPwd
     * @param request
     * @return String
     * @throws Exception
     */
    public static String shaEncdoing(String strPwd, HttpServletRequest request) throws Exception {
        boolean passYn = true;
        String result = "";
        KISA_SHA256 cncPass = new KISA_SHA256();
        String userAgent = request.getHeader("User-Agent").toUpperCase();

        //String strPwdDecoded = B64.decodeToString(strPwd);

        if (passYn == false) {
            result = "100";
        } else {
            result = cncPass.getEncCode(strPwd);
        }
//		if (userAgent.indexOf("7.0") != -1 || userAgent.indexOf("8.0") != -1
//				|| userAgent.indexOf("9.0") != -1
//				|| userAgent.indexOf("5.0") != -1) {
//
//		}
        return result;
    }


    // RSA 암호화
    /*
     * 공개키와 개인키 한 쌍 생성
     */
    public static HashMap<String, String> createKeypairAsString() {
        HashMap<String, String> stringKeypair = new HashMap<>();

        try {
            SecureRandom secureRandom = new SecureRandom();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048, secureRandom);
            KeyPair keyPair = keyPairGenerator.genKeyPair();

            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            String stringPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String stringPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

            stringKeypair.put("publicKey", stringPublicKey);
            stringKeypair.put("privateKey", stringPrivateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringKeypair;
    }

    /*
     * 암호화 : 공개키로 진행
     */
    public static String rsaEncrypt(String plainText, String stringPublicKey) {
        String encryptedText = null;

        try {
            // 평문으로 전달받은 공개키를 사용하기 위해 공개키 객체 생성
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] bytePublicKey = Base64.getDecoder().decode(stringPublicKey.getBytes());
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytePublicKey);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            // 만들어진 공개키 객체로 암호화 설정
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedText;
    }

    /*
     * 복호화 : 개인키로 진행
     */
    public static String rsaDecrypt(String encryptedText, String stringPrivateKey) {
        String decryptedText = null;

        try {
            // 평문으로 전달받은 공개키를 사용하기 위해 공개키 객체 생성
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] bytePrivateKey = Base64.getDecoder().decode(stringPrivateKey.getBytes());
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytePrivateKey);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            // 만들어진 공개키 객체로 복호화 설정
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            // 암호문을 평문화하는 과정
            byte[] encryptedBytes =  Base64.getDecoder().decode(encryptedText.getBytes());
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            decryptedText = new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decryptedText;
    }
}
