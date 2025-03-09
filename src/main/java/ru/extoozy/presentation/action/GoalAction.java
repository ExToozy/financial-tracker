package ru.extoozy.presentation.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.handler.goal.CreateGoalHandler;
import ru.extoozy.presentation.handler.goal.DeleteGoalHandler;
import ru.extoozy.presentation.handler.goal.GetGoalsHandler;
import ru.extoozy.presentation.handler.goal.ReplenishGoalHandler;

@Getter
@RequiredArgsConstructor
public enum GoalAction implements Action {

    CREATE_GOAL(new CreateGoalHandler(), "Создать цель"),
    REPLENISH_GOAL(new ReplenishGoalHandler(), "Пополнить цель"),
    GET_GOALS(new GetGoalsHandler(), "Получить цели"),
    DELETE_GOAL(new DeleteGoalHandler(), "Удалить цель");

    private final ActionHandler handler;
    private final String menuItemName;
}
