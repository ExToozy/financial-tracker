package ru.extoozy.presentation.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.handler.profile.UpdateNameHandler;
import ru.extoozy.presentation.handler.user.UpdateEmailHandler;
import ru.extoozy.presentation.handler.user.UpdatePasswordHandler;

@Getter
@RequiredArgsConstructor
public enum UserAction implements Action {

    UPDATE_PASSWORD(new UpdatePasswordHandler(), "Сменить пароль"),
    UPDATE_EMAIL(new UpdateEmailHandler(), "Сменить почту"),
    UPDATE_NAME(new UpdateNameHandler(), "Сменить имя пользователя");

    private final ActionHandler handler;
    private final String menuItemName;

}
