package ru.extoozy.presentation.handler.budget;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.controller.BudgetController;
import ru.extoozy.dto.budget.CreateBudgetDto;
import ru.extoozy.dto.budget.UpdateBudgetDto;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.in.ConsoleInHelper;
import ru.extoozy.out.ConsoleOutHelper;

import java.math.BigDecimal;

public class UpdateBudgetHandler implements ActionHandler {

    BudgetController budgetController = ApplicationContext.getBean(BudgetController.class);

    @Override
    public void handle() {
        ConsoleOutHelper.print("Введите месячный бюджет");
        double maxAmount = ConsoleInHelper.readDoubleRepeatable();
        try {
            budgetController.update(UpdateBudgetDto.builder()
                    .maxAmount(BigDecimal.valueOf(maxAmount))
                    .build()
            );
        } catch (ResourceNotFoundException e) {
            budgetController.create(CreateBudgetDto.builder()
                    .maxAmount(BigDecimal.valueOf(maxAmount))
                    .build()
            );
        }
        ConsoleOutHelper.print("Месячный бюджет успешно установлен");
    }
}
