package ru.extoozy.presentation.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.handler.budget.CheckBudgetHandler;
import ru.extoozy.presentation.handler.budget.SetBudgetHandler;
import ru.extoozy.presentation.handler.budget.UpdateBudgetHandler;

@Getter
@RequiredArgsConstructor
public enum BudgetAction implements Action {

    SET_BUDGET(new SetBudgetHandler(), "Установить месячный бюджет"),
    UPDATE_BUDGET(new UpdateBudgetHandler(), "Изменить месячный бюджет"),
    CHECK_BUDGET(new CheckBudgetHandler(), "Посмотреть текущий бюджет");

    private final ActionHandler handler;
    private final String menuItemName;
}
