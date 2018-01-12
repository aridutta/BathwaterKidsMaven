/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author rajeshk
 */
public class CryptoUtil {
    
    private final static String AES_ALGORITHM = "AES";
    
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    
    private final static String KEY = "D81BDD13234328D32D46FC24E2767";
    
    public static String encryptAES(String plainText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Key key = new SecretKeySpec(KEY.getBytes(), AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
    }
    
    public static String decryptAES(String cipherText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Key key = new SecretKeySpec(KEY.getBytes(), AES_ALGORITHM);
        
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        
        cipher.init(Cipher.DECRYPT_MODE, key);
        
        return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
    }
    
    public static String calculateHMACSignature(String plaintext) throws NoSuchAlgorithmException, InvalidKeyException {
        Key key = new SecretKeySpec(KEY.getBytes(), HMAC_SHA1_ALGORITHM);
        
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        
        mac.init(key);
        
        return Base64.getEncoder().encodeToString(mac.doFinal(plaintext.getBytes()));
    }
    
}
