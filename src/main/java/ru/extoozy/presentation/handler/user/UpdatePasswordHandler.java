package ru.extoozy.presentation.handler.user;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.controller.UserController;
import ru.extoozy.dto.user.UpdateUserDto;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.in.ConsoleInHelper;
import ru.extoozy.presentation.out.ConsoleOutHelper;

public class UpdatePasswordHandler implements ActionHandler {

    UserController userController = (UserController) ApplicationContext.getBean(UserController.class);

    @Override
    public void handle() {
        ConsoleOutHelper.print("Введите новый пароль");
        String password = ConsoleInHelper.readLine();

        userController.update(
                UpdateUserDto.builder()
                        .password(password)
                        .build()
        );
    }
}
