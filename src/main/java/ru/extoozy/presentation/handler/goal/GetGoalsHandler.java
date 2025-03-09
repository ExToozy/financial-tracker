package ru.extoozy.presentation.handler.goal;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.context.UserContext;
import ru.extoozy.controller.GoalController;
import ru.extoozy.dto.goal.GoalDto;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.out.ConsoleOutHelper;

import java.util.List;

public class GetGoalsHandler implements ActionHandler {
    GoalController goalController = (GoalController) ApplicationContext.getBean(GoalController.class);

    @Override
    public void handle() {
        UserProfileEntity userProfile = UserContext.getUser().getUserProfile();
        List<GoalDto> goals = goalController.getAllByUserProfileId(userProfile.getId());
        for (GoalDto goal : goals) {
            ConsoleOutHelper.print("-".repeat(20));
            ConsoleOutHelper.print("Идентификатор: %s".formatted(goal.getId()));
            ConsoleOutHelper.print("Название: %s".formatted(goal.getName()));
            ConsoleOutHelper.print("Нужно собрать: %s".formatted(goal.getGoalAmount()));
            ConsoleOutHelper.print("Собрано: %s".formatted(goal.getCurrentAmount()));
        }
        ConsoleOutHelper.print("-".repeat(20));
    }
}
