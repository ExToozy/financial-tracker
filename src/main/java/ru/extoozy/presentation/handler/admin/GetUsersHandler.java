package ru.extoozy.presentation.handler.admin;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.controller.UserController;
import ru.extoozy.dto.user.UserDto;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.out.ConsoleOutHelper;

import java.util.List;

public class GetUsersHandler implements ActionHandler {

    UserController userController = (UserController) ApplicationContext.getBean(UserController.class);

    @Override
    public void handle() {
        List<UserDto> users = userController.getAll();
        for (UserDto user : users) {
            ConsoleOutHelper.print("Идентификатор: %s".formatted(user.getId()));
            ConsoleOutHelper.print("Почта: %s".formatted(user.getEmail()));
            ConsoleOutHelper.print("Роль: %s".formatted(user.getRole()));
            ConsoleOutHelper.print("Заблокирован: %s".formatted(user.isBlocked() ? "да" : "нет"));
        }
    }
}
