package ru.extoozy.presentation.handler.transaction;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.controller.TransactionController;
import ru.extoozy.dto.transaction.TransactionDto;
import ru.extoozy.dto.transaction.UpdateTransactionDto;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.in.ConsoleInHelper;
import ru.extoozy.presentation.out.ConsoleOutHelper;

import java.math.BigDecimal;

public class UpdateTransactionHandler implements ActionHandler {

    TransactionController transactionController =
            (TransactionController) ApplicationContext.getBean(TransactionController.class);


    @Override
    public void handle() {
        try {
            ConsoleOutHelper.print("Введите идентификатор транзакции");
            Long transactionId = ConsoleInHelper.readLongRepeatable();
            TransactionDto transaction = transactionController.get(transactionId);

            var updateTransactionBuilder = UpdateTransactionDto.builder();

            if (ConsoleInHelper.getAnswer("Вы хотите изменить сумму?")) {
                ConsoleOutHelper.print("Введите новую сумму");
                Double amount = ConsoleInHelper.readDoubleRepeatable();
                updateTransactionBuilder.amount(BigDecimal.valueOf(amount));
            }

            if (ConsoleInHelper.getAnswer("Вы хотите изменить категорию?")) {
                ConsoleOutHelper.print("Введите новую категорию");
                String category = ConsoleInHelper.readLine();
                updateTransactionBuilder.category(category);
            }

            if (ConsoleInHelper.getAnswer("Вы хотите изменить описание?")) {
                ConsoleOutHelper.print("Введите новое описание");
                String description = ConsoleInHelper.readLine();
                updateTransactionBuilder.description(description);
            }
            transactionController.update(
                    updateTransactionBuilder
                            .id(transaction.getId())
                            .build()
            );
        } catch (ResourceNotFoundException e) {
            ConsoleOutHelper.print("Транзакции с таким id не найдено");
        }
    }
}
