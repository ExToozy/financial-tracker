package ru.extoozy.presentation.handler.goal;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.controller.GoalController;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.in.ConsoleInHelper;
import ru.extoozy.presentation.out.ConsoleOutHelper;

import java.math.BigDecimal;

public class ReplenishGoalHandler implements ActionHandler {

    GoalController goalController = (GoalController) ApplicationContext.getBean(GoalController.class);

    @Override
    public void handle() {
        ConsoleOutHelper.print("Введите идентификатор цели");
        Long goalId = ConsoleInHelper.readLongRepeatable();
        ConsoleOutHelper.print("Введите сумму на которую пополняете цель");
        Double amount = ConsoleInHelper.readDoubleRepeatable();

        try {
            goalController.replenish(goalId, BigDecimal.valueOf(amount));
        } catch (ResourceNotFoundException e) {
            ConsoleOutHelper.print("Цели с таким идентификатором не существует");
        }
    }
}
