package ru.extoozy.presentation.handler.profile;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.context.UserContext;
import ru.extoozy.controller.UserProfileController;
import ru.extoozy.dto.profile.UpdateUserProfileDto;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.in.ConsoleInHelper;
import ru.extoozy.out.ConsoleOutHelper;

public class UpdateNameHandler implements ActionHandler {
    private final UserProfileController userProfileController =
            ApplicationContext.getBean(UserProfileController.class);

    @Override
    public void handle() {
        String firstName = null;
        String lastName = null;

        if (ConsoleInHelper.getAnswer("Вы хотите изменить имя?")) {
            ConsoleOutHelper.print("Введите новое имя");
            firstName = ConsoleInHelper.readLine();
        }

        if (ConsoleInHelper.getAnswer("Вы хотите изменить фамилию?")) {
            ConsoleOutHelper.print("Введите новую фамилию");
            lastName = ConsoleInHelper.readLine();
        }

        userProfileController.update(
                UpdateUserProfileDto.builder()
                        .id(UserContext.getUser().getUserProfile().getId())
                        .firstName(firstName)
                        .lastName(lastName)
                        .build()
        );

    }
}
