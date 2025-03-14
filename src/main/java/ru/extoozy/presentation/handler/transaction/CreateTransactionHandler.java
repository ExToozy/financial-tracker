package ru.extoozy.presentation.handler.transaction;

import ru.extoozy.context.ApplicationContext;
import ru.extoozy.controller.TransactionController;
import ru.extoozy.dto.transaction.CreateTransactionDto;
import ru.extoozy.enums.TransactionType;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.in.ConsoleInHelper;
import ru.extoozy.out.ConsoleOutHelper;

import java.math.BigDecimal;

public class CreateTransactionHandler implements ActionHandler {

    TransactionController transactionController =
            ApplicationContext.getBean(TransactionController.class);

    @Override
    public void handle() {
        ConsoleOutHelper.print("Выберите тип транзакции");
        TransactionType transactionType = getTransactionType();
        ConsoleOutHelper.print("Введите сумму транзакции");
        Double amount = ConsoleInHelper.readDoubleRepeatable();
        ConsoleOutHelper.print("Введите категорию");
        String category = ConsoleInHelper.readLine();
        ConsoleOutHelper.print("Введите описание");
        String description = ConsoleInHelper.readLine();

        transactionController.create(
                CreateTransactionDto.builder()
                        .transactionType(transactionType)
                        .amount(BigDecimal.valueOf(amount))
                        .category(category)
                        .description(description)
                        .build()
        );

    }

    private TransactionType getTransactionType() {
        TransactionType transactionType;
        ConsoleOutHelper.print("1. Снятие");
        ConsoleOutHelper.print("2. Пополнение");
        while (true) {
            String answer = ConsoleInHelper.readLine();
            if (answer.equals("1")) {
                transactionType = TransactionType.WITHDRAWAL;
                break;
            } else if (answer.equals("2")) {
                transactionType = TransactionType.REPLENISHMENT;
                break;
            } else {
                ConsoleOutHelper.print("Введите 1 или 2");
            }
        }
        return transactionType;
    }
}
