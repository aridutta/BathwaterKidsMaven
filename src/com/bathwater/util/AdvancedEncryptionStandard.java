/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author rajeshk
 */
public class AdvancedEncryptionStandard {
    private final String encryptionKey;

    public AdvancedEncryptionStandard(String encryptionKey)
    {
        this.encryptionKey = encryptionKey;
    }

    public String encrypt(String plainText) throws Exception
    {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encrypted) throws Exception
    {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        byte[] plainBytes = cipher.doFinal(Base64.getDecoder().decode(encrypted));

        return new String(plainBytes);
    }

    private Cipher getCipher(int cipherMode)
            throws Exception
    {
        String encryptionAlgorithm = "AES";
        SecretKeySpec keySpecification = new SecretKeySpec(
                encryptionKey.getBytes("UTF-8"), encryptionAlgorithm);
        Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
        cipher.init(cipherMode, keySpecification);

        return cipher;
    }
}
