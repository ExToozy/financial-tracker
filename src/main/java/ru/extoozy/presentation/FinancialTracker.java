package ru.extoozy.presentation;

import lombok.RequiredArgsConstructor;
import ru.extoozy.context.UserContext;
import ru.extoozy.enums.UserRole;
import ru.extoozy.presentation.action.Action;
import ru.extoozy.presentation.action.AuthAction;
import ru.extoozy.presentation.action.ExitAction;
import ru.extoozy.presentation.action.ProfileAction;
import ru.extoozy.presentation.in.ConsoleInHelper;
import ru.extoozy.presentation.manager.ActionManager;
import ru.extoozy.presentation.manager.MenuManager;
import ru.extoozy.presentation.out.ConsoleOutHelper;

@RequiredArgsConstructor
public class FinancialTracker {

    private final ActionManager actionManager;
    private final MenuManager menuManager;

    public void runMainLoop() {
        try {
            runAuthLoop();
            if (UserContext.getUser().getRole() == UserRole.USER) {
                runUserLoop();
            } else if (UserContext.getUser().getRole() == UserRole.ADMIN) {
                runAdminLoop();
            }
        } finally {
            UserContext.clear();
        }
    }

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

    private void runUserLoop() {
        while (true) {
            if (UserContext.getUser().getUserProfile() == null) {
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
