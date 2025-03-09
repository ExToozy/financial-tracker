package ru.extoozy.presentation.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.handler.auth.AuthenticationHandler;
import ru.extoozy.presentation.handler.auth.RegisterHandler;

@Getter
@RequiredArgsConstructor
public enum AuthAction implements Action {

    AUTHORIZATION(new AuthenticationHandler(), "Авторизация"),
    REGISTRATION(new RegisterHandler(), "Регистрация");

    private final ActionHandler handler;
    private final String menuItemName;
}
