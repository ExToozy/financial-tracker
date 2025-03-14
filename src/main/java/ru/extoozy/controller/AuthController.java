package ru.extoozy.controller;

import lombok.RequiredArgsConstructor;
import ru.extoozy.dto.user.AuthUserDto;
import ru.extoozy.service.auth.AuthService;

/**
 * Контроллер для обработки аутентификации и регистрации пользователей.
 * Содержит методы для регистрации нового пользователя и аутентификации существующего.
 */
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Регистрирует нового пользователя.
     *
     * @param dto объект, содержащий данные для регистрации пользователя
     */
    public void register(AuthUserDto dto) {
        authService.register(dto);
    }

    /**
     * Выполняет аутентификацию пользователя.
     *
     * @param dto объект, содержащий данные для аутентификации пользователя
     */
    public void authenticate(AuthUserDto dto) {
        authService.authenticate(dto);
    }
}
