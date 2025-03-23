package ru.extoozy.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;

/**
 * Утилитарный класс для управления бинами в приложении.
 * Предоставляет статические методы для добавления и получения бинов по их типу класса.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationContext {
    /**
     * Внутреннее хранилище зарегистрированных бинов, сопоставленных по их типу класса.
     */
    private static final HashMap<Class<?>, Object> BEANS = new HashMap<>();


    /**
     * Добавляет бин в контекст приложения.
     *
     * @param clazz тип класса бина
     * @param bean  экземпляр бина для хранения
     */
    public static void addBean(Class<?> clazz, Object bean) {
        BEANS.put(clazz, bean);
    }

    /**
     * Получает бин из контекста приложения по его типу класса.
     *
     * @param clazz тип класса требуемого бина
     * @return экземпляр бина, если найден, иначе null
     */
    public static <T> T getBean(Class<T> clazz) {
        return clazz.cast(BEANS.get(clazz));
    }

}
