package ru.extoozy.presentation;

import lombok.RequiredArgsConstructor;
import ru.extoozy.context.UserContext;
import ru.extoozy.enums.UserRole;
import ru.extoozy.in.ConsoleInHelper;
import ru.extoozy.out.ConsoleOutHelper;
import ru.extoozy.presentation.action.Action;
import ru.extoozy.presentation.action.AuthAction;
import ru.extoozy.presentation.action.ExitAction;
import ru.extoozy.presentation.action.ProfileAction;
import ru.extoozy.presentation.manager.ActionManager;
import ru.extoozy.presentation.manager.MenuManager;

/**
 * Основной класс для запуска и управления финансовым трекером.
 */
@RequiredArgsConstructor
public class FinancialTracker {

    private final ActionManager actionManager;
    private final MenuManager menuManager;

    /**
     * Основной цикл выполнения программы.
     * Осуществляет аутентификацию и переход в соответствующий режим работы
     * (пользовательский или административный).
     */
    public void runMainLoop() {
        try {
            while (true) {
                runAuthLoop();
                if (UserContext.getUser().getRole() == UserRole.USER) {
                    runUserLoop();
                } else if (UserContext.getUser().getRole() == UserRole.ADMIN) {
                    runAdminLoop();
                }
                boolean answer = ConsoleInHelper.getAnswer("Вы хотите снова авторизоваться?");
                if (!answer) {
                    break;
                }
            }
        } finally {
            UserContext.clear();
        }
    }

    /**
     * Цикл аутентификации. Позволяет пользователю выбрать авторизацию или регистрацию.
     * Повторяется до успешной аутентификации.
     */
    private void runAuthLoop() {
        while (true) {
            ConsoleOutHelper.print("Выберите пункт");
            ConsoleOutHelper.print("1. Авторизация");
            ConsoleOutHelper.print("2. Регистрация");
            while (true) {
                String answer = ConsoleInHelper.readLine();
                if (answer.equals("1")) {
                    actionManager.doAction(AuthAction.AUTHORIZATION);
                    break;
                } else if (answer.equals("2")) {
                    actionManager.doAction(AuthAction.REGISTRATION);
                    break;
                } else {
                    ConsoleOutHelper.print("Введите 1 или 2");
                }
            }
            if (UserContext.getUser() == null) {
                ConsoleOutHelper.print("Пользователь не авторизован! Попробуйте снова");
                continue;
            }
            break;
        }
    }

    /**
     * Цикл работы с пользовательским меню.
     * Если профиль пользователя отсутствует, предлагает его создать.
     * Позволяет пользователю выбирать действия из меню до выхода.
     */
    private void runUserLoop() {
        while (true) {
            if (UserContext.getUser().getUserProfile().getId() == 0) {
                actionManager.doAction(ProfileAction.CREATE_PROFILE);
            } else {
                menuManager.printUserMenu();
                String menuItemNumber = ConsoleInHelper.getMenuItemNumber();
                Action action = menuManager.getActionFromUserMenu(menuItemNumber);
                actionManager.doAction(action);
                if (action == ExitAction.EXIT) {
                    break;
                }
            }
        }
    }

    /**
     * Цикл работы с административным меню.
     * Позволяет администратору выбирать действия из меню до выхода.
     */
    private void runAdminLoop() {
        while (true) {
            menuManager.printAdminMenu();
            String menuItemNumber = ConsoleInHelper.getMenuItemNumber();
            Action action = menuManager.getActionFromAdminMenu(menuItemNumber);
            actionManager.doAction(action);
            if (action == ExitAction.EXIT) {
                break;
            }
        }
    }
}
