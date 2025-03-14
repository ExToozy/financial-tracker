package ru.extoozy.presentation.action;

import ru.extoozy.presentation.handler.ActionHandler;

/**
 * Интерфейс, представляющий действие в системе.
 * Каждое действие должно предоставлять обработчик и наименование для пункта меню.
 */
public interface Action {

    /**
     * Получает обработчик действия.
     *
     * @return экземпляр {@link ActionHandler}, реализующий логику данного действия
     */
    ActionHandler getHandler();

    /**
     * Получает название пункта меню, связанного с этим действием.
     *
     * @return строка с названием пункта меню
     */
    String getMenuItemName();
}
