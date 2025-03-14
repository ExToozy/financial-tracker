package ru.extoozy.presentation.handler.profile;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.controller.UserProfileController;
import ru.extoozy.dto.profile.CreateUserProfileDto;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.in.ConsoleInHelper;
import ru.extoozy.out.ConsoleOutHelper;

public class CreateProfileHandler implements ActionHandler {

    private final UserProfileController userProfileController =
            ApplicationContext.getBean(UserProfileController.class);

    @Override
    public void handle() {
        ConsoleOutHelper.print("Теперь давайте знакомиться!");
        ConsoleOutHelper.print("Введите ваше имя");
        String firstName = ConsoleInHelper.readLine();
        ConsoleOutHelper.print("Введите вашу фамилию");
        String lastName = ConsoleInHelper.readLine();

        userProfileController.create(
                CreateUserProfileDto.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .build()
        );
    }
}
