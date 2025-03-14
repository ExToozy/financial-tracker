package ru.extoozy.presentation.manager;

import ru.extoozy.presentation.action.Action;

/**
 * Класс, отвечающий за выполнение действий, реализующих интерфейс {@link Action}.
 */
public class ActionManager {

    /**
     * Выполняет указанное действие, вызывая его обработчик.
     *
     * @param action действие, которое необходимо выполнить
     */
    public void doAction(Action action) {
        action.getHandler().handle();
    }
}
