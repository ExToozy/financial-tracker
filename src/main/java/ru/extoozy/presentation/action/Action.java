package ru.extoozy.presentation.action;

import ru.extoozy.presentation.handler.ActionHandler;

public interface Action {
    ActionHandler getHandler();

    String getMenuItemName();
}
