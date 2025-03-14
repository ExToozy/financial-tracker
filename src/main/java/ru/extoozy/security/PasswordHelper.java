package ru.extoozy.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Утилитный класс для работы с паролями, обеспечивающий хеширование паролей
 * с использованием алгоритма PBKDF2 (Password-Based Key Derivation Function 2).
 * Класс предоставляет методы для проверки совпадения пароля с хешем
 * и для получения хеша пароля.
 */
public class PasswordHelper {
    
    private static final String SALT = "mpDhaJZfpFZBEOovhD6z2g==";

    /**
     * Проверяет, совпадает ли пароль с хешем пароля пользователя.
     *
     * @param password           пароль, который нужно проверить
     * @param hashedUserPassword хеш пароля пользователя для сравнения
     * @return {@code true}, если пароль совпадает с хешем, иначе {@code false}
     */
    public static boolean checkPasswordEquals(String password, String hashedUserPassword) {
        String passwordHash = getPasswordHash(password);
        return hashedUserPassword.equals(passwordHash);
    }

    /**
     * Генерирует хеш пароля с использованием алгоритма PBKDF2 с солью.
     *
     * @param password пароль, для которого нужно получить хеш
     * @return хеш пароля в виде строки, закодированной в Base64
     */
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
