package com.Lobo.KakaoLoginTest.Utility;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.HashMap;

@Component
public class AESUtil {

    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "YourSecretKey123"; // 반드시 16, 24, 또는 32바이트

    public static String encrypt(String input) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedInput) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedInput);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    public static HashMap<String, String> encryptTokens(HashMap<String, String> tokens) throws Exception {
        HashMap<String, String> encryptedTokens = new HashMap<>(tokens);
        String encryptedAccessToken = encrypt(tokens.get("access_token"));
        String encryptedRefreshToken = encrypt(tokens.get("refresh_token"));

        encryptedTokens.replace("access_token", encryptedAccessToken);
        encryptedTokens.replace("refresh_token", encryptedRefreshToken);

        return encryptedTokens;
    }

    public static HashMap<String, String> decryptTokens(HashMap<String, String> tokens) throws Exception {
        HashMap<String, String> decryptedTokens = new HashMap<>(tokens);
        String decryptedAccessToken = decrypt(tokens.get("access_token"));
        String decryptedRefreshToken = decrypt(tokens.get("refresh_token"));

        decryptedTokens.replace("access_token", decryptedAccessToken);
        decryptedTokens.replace("refresh_token", decryptedRefreshToken);

        return decryptedTokens;
    }
}
