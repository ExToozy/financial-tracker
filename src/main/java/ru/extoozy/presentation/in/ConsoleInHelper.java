package ru.extoozy.presentation.in;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.presentation.out.ConsoleOutHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConsoleInHelper {

    private static Scanner SCANNER = new Scanner(System.in);

    public static String readLine() {
        return SCANNER.nextLine();
    }

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
