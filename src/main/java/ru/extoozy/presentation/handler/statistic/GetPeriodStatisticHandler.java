package ru.extoozy.presentation.handler.statistic;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.context.UserContext;
import ru.extoozy.controller.TransactionStatisticController;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.in.ConsoleInHelper;
import ru.extoozy.presentation.out.ConsoleOutHelper;

import java.time.LocalDate;
import java.util.Map;

public class GetPeriodStatisticHandler implements ActionHandler {

    private final TransactionStatisticController transactionStatisticController =
            (TransactionStatisticController) ApplicationContext.getBean(TransactionStatisticController.class);

    @Override
    public void handle() {
        ConsoleOutHelper.print("Введите дату начала");
        LocalDate start = ConsoleInHelper.getDate();
        ConsoleOutHelper.print("Введите дату конца");
        LocalDate end = ConsoleInHelper.getDate();

        UserProfileEntity userProfile = UserContext.getUser().getUserProfile();

        Map<String, Object> userStatistic = transactionStatisticController.getUserStatisticByPeriod(
                userProfile.getId(),
                start,
                end
        );

        ConsoleOutHelper.print("Текущий баланс: %s".formatted(userStatistic.get("total_sum")));
        ConsoleOutHelper.print("Всего транзакций было совершено: %s".formatted(userStatistic.get("transaction_count")));
        ConsoleOutHelper.print("Сумма снятий: %s".formatted(userStatistic.get("withdrawal_sum")));
        ConsoleOutHelper.print("Сумма пополнений: %s".formatted(userStatistic.get("replenish_sum")));
    }
}
