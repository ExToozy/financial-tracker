package ru.extoozy.presentation.handler.statistic;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.context.UserContext;
import ru.extoozy.controller.TransactionStatisticController;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.out.ConsoleOutHelper;

import java.math.BigDecimal;
import java.util.Map;

public class GetFullStatisticHandler implements ActionHandler {

    private final TransactionStatisticController transactionStatisticController =
            ApplicationContext.getBean(TransactionStatisticController.class);

    @Override
    public void handle() {
        UserProfileEntity userProfile = UserContext.getUser().getUserProfile();
        Map<String, Object> userStatistic = transactionStatisticController.getFullUserStatistic(userProfile.getId());
        ConsoleOutHelper.print("Текущий баланс: %s".formatted(userStatistic.get("total_sum")));
        ConsoleOutHelper.print("Всего транзакций было совершено: %s".formatted(userStatistic.get("transaction_count")));
        ConsoleOutHelper.print("Сумма снятий: %s".formatted(userStatistic.get("withdrawal_sum")));
        ConsoleOutHelper.print("Сумма пополнений: %s".formatted(userStatistic.get("replenish_sum")));
        ConsoleOutHelper.print("Суммы снятий по категориям:");
        Map<String, BigDecimal> groupedByCategory = (Map<String, BigDecimal>) userStatistic.get("grouped_by_category");
        if (groupedByCategory.isEmpty()) {
            ConsoleOutHelper.print("Снятий не было");
        } else {
            for (var entry : groupedByCategory.entrySet()) {
                ConsoleOutHelper.print("В категории '%s': %s".formatted(entry.getKey(), entry.getValue()));
            }
        }

    }
}
