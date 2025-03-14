package ru.extoozy.presentation.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.handler.statistic.GetCurrentBalanceHandler;
import ru.extoozy.presentation.handler.statistic.GetFullStatisticHandler;
import ru.extoozy.presentation.handler.statistic.GetPeriodStatisticHandler;

@Getter
@RequiredArgsConstructor
public enum StatisticAction implements Action {

    GET_CURRENT_BALANCE(new GetCurrentBalanceHandler(), "Текущий баланс"),
    GET_PERIOD_STATISTIC(new GetPeriodStatisticHandler(), "Просмотр доходов и расходов"),
    GET_FULL_STATISTIC(new GetFullStatisticHandler(), "Просмотр полной статистики");

    private final ActionHandler handler;
    private final String menuItemName;

}
