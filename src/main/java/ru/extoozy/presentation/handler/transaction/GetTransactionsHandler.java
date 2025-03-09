package ru.extoozy.presentation.handler.transaction;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.context.UserContext;
import ru.extoozy.controller.TransactionController;
import ru.extoozy.dto.transaction.TransactionDto;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.out.ConsoleOutHelper;

import java.util.List;

public class GetTransactionsHandler implements ActionHandler {

    TransactionController transactionController =
            (TransactionController) ApplicationContext.getBean(TransactionController.class);

    @Override
    public void handle() {
        Long userProfileId = UserContext.getUser().getUserProfile().getId();
        List<TransactionDto> transactions = transactionController.getAllByUserProfileId(userProfileId);

        for (TransactionDto transaction : transactions) {
            ConsoleOutHelper.print("Идентификатор: %s".formatted(transaction.getId()));
            ConsoleOutHelper.print("Тип: %s".formatted(transaction.getTransactionType()));
            ConsoleOutHelper.print("Сумма: %s".formatted(transaction.getAmount()));
            ConsoleOutHelper.print("Категория: %s".formatted(transaction.getCategory()));
            ConsoleOutHelper.print("Описание: %s".formatted(transaction.getDescription()));
            ConsoleOutHelper.print("Время создания: %s".formatted(transaction.getCreatedAt()));
        }
    }
}
