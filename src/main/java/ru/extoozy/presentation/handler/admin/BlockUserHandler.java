package ru.extoozy.presentation.handler.admin;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.controller.UserController;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.in.ConsoleInHelper;
import ru.extoozy.out.ConsoleOutHelper;

public class BlockUserHandler implements ActionHandler {

    UserController userController = ApplicationContext.getBean(UserController.class);

    @Override
    public void handle() {
        ConsoleOutHelper.print("Введите идентификатор пользователя");
        Long userId = ConsoleInHelper.readLongRepeatable();

        try {
            userController.changeBlockStatus(userId);
        } catch (ResourceNotFoundException e) {
            ConsoleOutHelper.print("Пользователя с таким идентификатором не существует");
        }
    }
}
