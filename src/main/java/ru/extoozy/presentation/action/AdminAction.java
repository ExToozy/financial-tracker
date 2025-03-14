package ru.extoozy.presentation.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.handler.admin.BlockUserHandler;
import ru.extoozy.presentation.handler.admin.GetUserTransactionsHandler;
import ru.extoozy.presentation.handler.admin.GetUsersHandler;

@Getter
@RequiredArgsConstructor
public enum AdminAction implements Action {

    GET_USERS(new GetUsersHandler(), "Получить список пользователей"),
    GET_USER_TRANSACTIONS(new GetUserTransactionsHandler(), "Получить транзакции пользователя"),
    BLOCK_USER(new BlockUserHandler(), "Сменить статус блокировки пользователя");

    private final ActionHandler handler;
    private final String menuItemName;
}
