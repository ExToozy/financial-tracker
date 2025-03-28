package ru.extoozy.presentation.handler.transaction;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.controller.TransactionController;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.in.ConsoleInHelper;
import ru.extoozy.out.ConsoleOutHelper;

public class DeleteTransactionHandler implements ActionHandler {

    TransactionController transactionController =
            ApplicationContext.getBean(TransactionController.class);

    @Override
    public void handle() {
        ConsoleOutHelper.print("Введите идентификатор транзакции");
        Long transactionId = ConsoleInHelper.readLongRepeatable();
        transactionController.delete(transactionId);
    }
}
