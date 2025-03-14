package ru.extoozy.presentation.handler.budget;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.context.UserContext;
import ru.extoozy.controller.BudgetController;
import ru.extoozy.dto.budget.CreateBudgetDto;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.in.ConsoleInHelper;
import ru.extoozy.out.ConsoleOutHelper;

import java.math.BigDecimal;

public class SetBudgetHandler implements ActionHandler {

    BudgetController budgetController = ApplicationContext.getBean(BudgetController.class);

    @Override
    public void handle() {
        try {
            budgetController.getByUserProfileIdAndCurrentMonth(UserContext.getUser().getUserProfile().getId());
            ConsoleOutHelper.print("Месячный бюджет уже установлен");
        } catch (ResourceNotFoundException e) {
            ConsoleOutHelper.print("Введите месячный бюджет");
            Double maxAmount = ConsoleInHelper.readDoubleRepeatable();
            budgetController.create(CreateBudgetDto
                    .builder()
                    .maxAmount(BigDecimal.valueOf(maxAmount))
                    .build()
            );
            ConsoleOutHelper.print("Месячный бюджет успешно установлен");
        }

    }
}
