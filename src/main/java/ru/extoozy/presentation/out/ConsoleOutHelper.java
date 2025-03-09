package ru.extoozy.presentation.out;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConsoleOutHelper {

    public static void print(String str) {
        System.out.println(str);
    }
}
