package ru.extoozy.presentation.handler.auth;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.controller.AuthController;
import ru.extoozy.dto.user.AuthUserDto;
import ru.extoozy.exception.UserAlreadyExistsException;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.in.ConsoleInHelper;
import ru.extoozy.presentation.out.ConsoleOutHelper;

public class RegisterHandler implements ActionHandler {

    private final AuthController authController = (AuthController) ApplicationContext.getBean(AuthController.class);

    @Override
    public void handle() {
        ConsoleOutHelper.print("Введите почту");
        String email = ConsoleInHelper.readLine();
        ConsoleOutHelper.print("Введите пароль");
        String password = ConsoleInHelper.readLine();

        try {
            authController.register(
                    AuthUserDto.builder()
                            .email(email)
                            .password(password)
                            .build()
            );
        } catch (UserAlreadyExistsException e) {
            ConsoleOutHelper.print("Пользователь с такой почтой уже существует");
        }
    }
}
