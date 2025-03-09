package ru.extoozy.presentation.manager;

import ru.extoozy.presentation.action.Action;

public class ActionManager {

    public void doAction(Action action) {
        action.getHandler().handle();
    }

}
