package ru.extoozy.presentation.handler.exit;

import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.out.ConsoleOutHelper;

public class ExitHandler implements ActionHandler {
    @Override
    public void handle() {
        ConsoleOutHelper.print("Всего доброго! Спасибо что пользовались нашим сервисом");
    }
}
