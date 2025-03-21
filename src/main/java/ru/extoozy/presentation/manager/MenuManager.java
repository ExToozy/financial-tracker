package ru.extoozy.presentation.manager;

import ru.extoozy.out.ConsoleOutHelper;
import ru.extoozy.presentation.action.Action;
import ru.extoozy.presentation.action.AdminAction;
import ru.extoozy.presentation.action.BudgetAction;
import ru.extoozy.presentation.action.ExitAction;
import ru.extoozy.presentation.action.GoalAction;
import ru.extoozy.presentation.action.StatisticAction;
import ru.extoozy.presentation.action.TransactionAction;
import ru.extoozy.presentation.action.UserAction;

import java.util.HashMap;

/**
 * Класс, управляющий отображением меню и выбором действий в системе.
 */
public class MenuManager {

    private final HashMap<Integer, Class<? extends Action>> userMenu;
    private final HashMap<Integer, Class<? extends Action>> adminMenu;

    /**
     * Инициализирует меню для пользователей и администраторов.
     */
    public MenuManager() {
        userMenu = new HashMap<>();
        userMenu.put(1, BudgetAction.class);
        userMenu.put(2, TransactionAction.class);
        userMenu.put(3, GoalAction.class);
        userMenu.put(4, StatisticAction.class);
        userMenu.put(5, UserAction.class);
        userMenu.put(6, ExitAction.class);

        adminMenu = new HashMap<>();
        adminMenu.put(1, AdminAction.class);
        adminMenu.put(2, ExitAction.class);
    }

    /**
     * Возвращает действие, выбранное из пользовательского меню.
     *
     * @param menuItemNumber номер пункта меню
     * @return выбранное действие или {@code null}, если пункт меню некорректен
     */
    public Action getActionFromUserMenu(String menuItemNumber) {
        return getActionFromMenu(menuItemNumber, userMenu);
    }

    /**
     * Возвращает действие, выбранное из меню администратора.
     *
     * @param menuItemNumber номер пункта меню
     * @return выбранное действие или {@code null}, если пункт меню некорректен
     */
    public Action getActionFromAdminMenu(String menuItemNumber) {
        return getActionFromMenu(menuItemNumber, adminMenu);
    }

    /**
     * Общий метод для получения действия из указанного меню.
     *
     * @param menuItemNumber номер пункта меню
     * @param menu           меню, из которого выбирается действие
     * @return выбранное действие или {@code null}, если пункт меню некорректен
     */
    private Action getActionFromMenu(String menuItemNumber, HashMap<Integer, Class<? extends Action>> menu) {
        int actionNumber = Integer.parseInt(menuItemNumber.split("\\.")[0]);
        Class<? extends Action> action = menu.get(actionNumber);
        if (action == null) {
            ConsoleOutHelper.print("Такого пункта нет");
            return null;
        }

        int subActionNumber = Integer.parseInt(menuItemNumber.split("\\.")[1]);
        int subActionIndex = subActionNumber - 1;
        Action[] subActions = action.getEnumConstants();
        if (subActionIndex < 0 || subActionIndex > subActions.length + 1) {
            ConsoleOutHelper.print("Такого пункта нет");
            return null;
        }

        return subActions[subActionIndex];
    }

    /**
     * Печатает пользовательское меню в консоль.
     */
    public void printUserMenu() {
        printMenu(userMenu);
    }

    /**
     * Печатает меню администратора в консоль.
     */
    public void printAdminMenu() {
        printMenu(adminMenu);
    }

    /**
     * Печатает указанное меню в консоль.
     *
     * @param menu меню для отображения
     */
    private void printMenu(HashMap<Integer, Class<? extends Action>> menu) {
        ConsoleOutHelper.print("-".repeat(20));
        for (var menuItem : menu.entrySet()) {
            Action[] menuItemActions = menuItem.getValue().getEnumConstants();
            for (int menuIndex = 0; menuIndex < menuItemActions.length; menuIndex++) {
                var action = menuItemActions[menuIndex];
                ConsoleOutHelper.print("%d.%d %s".formatted(menuItem.getKey(), menuIndex + 1, action.getMenuItemName()));
            }
            ConsoleOutHelper.print("-".repeat(20));
        }
    }
}
