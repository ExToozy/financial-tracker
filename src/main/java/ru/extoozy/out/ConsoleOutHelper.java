package ru.extoozy.out;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Утилитарный класс для вывода данных в консоль.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConsoleOutHelper {

    /**
     * Выводит строку в стандартный поток вывода.
     *
     * @param str строка для вывода в консоль
     */
    public static void print(String str) {
        System.out.println(str);
    }
}
