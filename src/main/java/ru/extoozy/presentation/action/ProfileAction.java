package ru.extoozy.presentation.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.extoozy.presentation.handler.ActionHandler;
import ru.extoozy.presentation.handler.profile.CreateProfileHandler;

@RequiredArgsConstructor
@Getter
public enum ProfileAction implements Action {

    CREATE_PROFILE(new CreateProfileHandler(), "Создать профиль");

    private final ActionHandler handler;
    private final String menuItemName;
}
