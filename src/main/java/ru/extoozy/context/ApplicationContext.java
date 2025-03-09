package ru.extoozy.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationContext {

    private static final HashMap<Class<?>, Object> BEANS = new HashMap<>();

    public static void addBean(Class<?> clazz, Object bean) {
        BEANS.put(clazz, bean);
    }

    public static Object getBean(Class<?> clazz) {
        return BEANS.get(clazz);
    }

}
