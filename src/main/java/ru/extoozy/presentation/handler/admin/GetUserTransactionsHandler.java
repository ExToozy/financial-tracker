package ru.extoozy.presentation.handler.admin;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.controller.TransactionController;
import ru.extoozy.controller.UserProfileController;
import ru.extoozy.dto.profile.UserProfileDto;
import ru.extoozy.dto.transaction.TransactionDto;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.in.ConsoleInHelper;
import ru.extoozy.presentation.out.ConsoleOutHelper;

import java.util.List;

public class GetUserTransactionsHandler implements ActionHandler {

    TransactionController transactionController =
            (TransactionController) ApplicationContext.getBean(TransactionController.class);

    UserProfileController userProfileController =
            (UserProfileController) ApplicationContext.getBean(UserProfileController.class);

    @Override
    public void handle() {
        ConsoleOutHelper.print("Введите идентификатор пользователя");
        Long userId = ConsoleInHelper.readLongRepeatable();
        UserProfileDto userProfile = userProfileController.getByUserId(userId);
        List<TransactionDto> transactions = transactionController.getAllByUserProfileId(userProfile.getId());

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
