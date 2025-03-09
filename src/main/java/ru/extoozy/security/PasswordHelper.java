package ru.extoozy.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class PasswordHelper {

    private static final String SALT = "mpDhaJZfpFZBEOovhD6z2g==";

    public static boolean checkPasswordEquals(String password, String hashedUserPassword) {
        String passwordHash = getPasswordHash(password);
        return hashedUserPassword.equals(passwordHash);
    }

    public static String getPasswordHash(String password) {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(SALT), 65536, 128);
        String passwordHash = null;
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            passwordHash = Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.err.print(e);
        }
        return passwordHash;
    }
}
