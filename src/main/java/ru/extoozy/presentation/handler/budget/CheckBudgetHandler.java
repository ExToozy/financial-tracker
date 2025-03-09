package ru.extoozy.presentation.handler.budget;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.context.UserContext;
import ru.extoozy.controller.BudgetController;
import ru.extoozy.dto.budget.BudgetDto;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.out.ConsoleOutHelper;

public class CheckBudgetHandler implements ActionHandler {

    BudgetController budgetController = (BudgetController) ApplicationContext.getBean(BudgetController.class);

    @Override
    public void handle() {
        try {
            UserProfileEntity userProfile = UserContext.getUser().getUserProfile();
            BudgetDto budget = budgetController.getByUserProfileIdAndCurrentMonth(userProfile.getId());

            ConsoleOutHelper.print("Ваш текущий бюджет: %s".formatted(budget.getMaxAmount()));
            ConsoleOutHelper.print("В текущем месяце вы потратили: %s".formatted(budget.getCurrentAmount()));
        } catch (ResourceNotFoundException e) {
            ConsoleOutHelper.print("Бюджет не установлен");
        }
    }
}
