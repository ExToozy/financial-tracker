package ru.extoozy.presentation.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.handler.exit.ExitHandler;

@Getter
@RequiredArgsConstructor
public enum ExitAction implements Action {

    EXIT(new ExitHandler(), "Выход");

    private final ActionHandler handler;
    private final String menuItemName;

}
