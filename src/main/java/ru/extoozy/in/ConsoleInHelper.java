package ru.extoozy.in;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.out.ConsoleOutHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Утилитарный класс для чтения данных из консоли.
 * Предоставляет методы для безопасного ввода данных различных типов.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConsoleInHelper {


    private static Scanner SCANNER = new Scanner(System.in);

    /**
     * Читает строку из консоли.
     *
     * @return введенная строка
     */
    public static String readLine() {
        return SCANNER.nextLine();
    }

    /**
     * Читает число типа {@code Double}, запрашивая повторный ввод в случае ошибки.
     *
     * @return корректное значение типа {@code Double}
     */
    public static Double readDoubleRepeatable() {
        while (true) {
            String doubleStr = SCANNER.nextLine();
            try {
                return Double.parseDouble(doubleStr);
            } catch (NumberFormatException e) {
                ConsoleOutHelper.print("Введите корректное число");
            }
        }
    }

    /**
     * Читает число типа {@code Long}, запрашивая повторный ввод в случае ошибки.
     *
     * @return корректное значение типа {@code Long}
     */
    public static Long readLongRepeatable() {
        while (true) {
            String doubleStr = SCANNER.nextLine();
            try {
                return Long.parseLong(doubleStr);
            } catch (NumberFormatException e) {
                ConsoleOutHelper.print("Введите корректное число");
            }
        }
    }

    /**
     * Запрашивает у пользователя подтверждение (да/нет).
     *
     * @param question текст вопроса
     * @return {@code true}, если пользователь ввел "да", иначе {@code false}
     */
    public static boolean getAnswer(String question) {
        ConsoleOutHelper.print("%s да/нет".formatted(question));
        while (true) {
            String answer = ConsoleInHelper.readLine();
            if (answer.equalsIgnoreCase("да")) {
                return true;
            } else if (answer.equalsIgnoreCase("нет")) {
                return false;
            }
            ConsoleOutHelper.print("Введите да или нет");
        }
    }

    /**
     * Запрашивает у пользователя корректный пункт меню в формате "X.X".
     *
     * @return строка с корректным номером пункта меню
     */
    public static String getMenuItemNumber() {
        while (true) {
            String menuItemNumber = ConsoleInHelper.readLine();
            if (menuItemNumber.matches("\\d\\.\\d")) {
                return menuItemNumber;
            } else {
                ConsoleOutHelper.print("Введите корректный пункт меню");
            }
        }
    }

    /**
     * Запрашивает у пользователя дату в формате "yyyy-MM-dd".
     *
     * @return объект {@link LocalDate} при успешном вводе
     */
    public static LocalDate getDate() {
        while (true) {
            String dateStr = ConsoleInHelper.readLine();
            try {
                return DateTimeFormatter.ofPattern("yyyy-MM-dd").parse(dateStr, LocalDate::from);
            } catch (DateTimeParseException e) {
                ConsoleOutHelper.print("Введите корректную дату. Например 2025-12-23");
            }
        }
    }
}
