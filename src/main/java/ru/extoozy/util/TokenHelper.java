package ru.extoozy.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Утилита для работы с токенами аутентификации.
 * Содержит методы для проверки валидности токена и извлечения идентификатора пользователя из токена.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenHelper {

    /**
     * Проверяет, является ли токен валидным.
     *
     * @param token токен для проверки
     * @return true, если токен валиден; иначе false
     */
    public static boolean isValidToken(String token) {
        return token != null && !token.isBlank() && !RegexUtil.isInvalidToken(token);
    }

    /**
     * Извлекает идентификатор пользователя из токена.
     *
     * @param token токен, содержащий идентификатор пользователя
     * @return идентификатор пользователя
     */
    public static Long getUserIdFromToken(String token) {
        return Long.parseLong(token.split(" ")[1]);
    }
}
