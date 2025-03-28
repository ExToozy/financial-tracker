package ru.extoozy.presentation.handler.goal;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.controller.GoalController;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.in.ConsoleInHelper;
import ru.extoozy.out.ConsoleOutHelper;

public class DeleteGoalHandler implements ActionHandler {
    GoalController goalController = ApplicationContext.getBean(GoalController.class);

    @Override
    public void handle() {
        ConsoleOutHelper.print("Введите идентификатор цели");
        Long goalId = ConsoleInHelper.readLongRepeatable();

        try {
            goalController.delete(goalId);
        } catch (ResourceNotFoundException e) {
            ConsoleOutHelper.print("У вас такой цели не существует");
        }

    }
}
