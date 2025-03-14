package ru.extoozy.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.entity.UserEntity;

/**
 * Контекст для хранения информации о текущем пользователе в рамках потока.
 * Предоставляет методы для установки, получения и очистки данных пользователя.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserContext {

    /**
     * Хранит информацию о текущем пользователе на уровне потока.
     */
    private static final ThreadLocal<UserEntity> USER_HOLDER = new ThreadLocal<>();

    /**
     * Возвращает текущего пользователя.
     *
     * @return объект {@link UserEntity} или null, если пользователь не установлен
     */
    public static UserEntity getUser() {
        return USER_HOLDER.get();
    }

    /**
     * Устанавливает текущего пользователя.
     *
     * @param user объект {@link UserEntity}, представляющий текущего пользователя
     */
    public static void setUser(UserEntity user) {
        USER_HOLDER.set(user);
    }

    /**
     * Очищает информацию о текущем пользователе из контекста потока.
     */
    public static void clear() {
        USER_HOLDER.remove();
    }

}