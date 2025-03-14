package ru.extoozy.presentation.handler.statistic;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.context.UserContext;
import ru.extoozy.controller.TransactionStatisticController;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.out.ConsoleOutHelper;

import java.math.BigDecimal;

public class GetCurrentBalanceHandler implements ActionHandler {

    private final TransactionStatisticController transactionStatisticController =
            ApplicationContext.getBean(TransactionStatisticController.class);

    @Override
    public void handle() {
        UserProfileEntity userProfile = UserContext.getUser().getUserProfile();
        BigDecimal userBalance = transactionStatisticController.getUserBalance(userProfile.getId());
        ConsoleOutHelper.print("Текущий баланс: %s".formatted(userBalance));
    }
}
