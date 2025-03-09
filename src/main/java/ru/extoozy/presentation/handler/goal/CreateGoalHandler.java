package ru.extoozy.presentation.handler.goal;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.controller.GoalController;
import ru.extoozy.dto.goal.CreateGoalDto;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.in.ConsoleInHelper;
import ru.extoozy.presentation.out.ConsoleOutHelper;

import java.math.BigDecimal;

public class CreateGoalHandler implements ActionHandler {

    GoalController goalController = (GoalController) ApplicationContext.getBean(GoalController.class);

    @Override
    public void handle() {
        ConsoleOutHelper.print("Введите название цели");
        String name = ConsoleInHelper.readLine();
        ConsoleOutHelper.print("Введите сколько нужно денег");
        Double amount = ConsoleInHelper.readDoubleRepeatable();

        goalController.create(
                CreateGoalDto
                        .builder()
                        .name(name)
                        .goalAmount(BigDecimal.valueOf(amount))
                        .build()
        );

        ConsoleOutHelper.print("Цель успешно создана");
    }
}
