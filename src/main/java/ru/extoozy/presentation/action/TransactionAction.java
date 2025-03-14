package ru.extoozy.presentation.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.handler.transaction.CreateTransactionHandler;
import ru.extoozy.presentation.handler.transaction.DeleteTransactionHandler;
import ru.extoozy.presentation.handler.transaction.GetTransactionsHandler;
import ru.extoozy.presentation.handler.transaction.UpdateTransactionHandler;

@Getter
@RequiredArgsConstructor
public enum TransactionAction implements Action {

    CREATE_TRANSACTION(new CreateTransactionHandler(), "Создать транзакцию"),
    UPDATE_TRANSACTION(new UpdateTransactionHandler(), "Редактировать транзакцию"),
    DELETE_TRANSACTION(new DeleteTransactionHandler(), "Удалить транзакцию"),
    GET_TRANSACTIONS(new GetTransactionsHandler(), "Получить все транзакции");

    private final ActionHandler handler;
    private final String menuItemName;

}
