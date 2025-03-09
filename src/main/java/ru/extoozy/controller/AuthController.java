package ru.extoozy.controller;

import lombok.RequiredArgsConstructor;
import ru.extoozy.dto.user.AuthUserDto;
import ru.extoozy.service.auth.AuthService;

@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    public void register(AuthUserDto dto) {
        authService.register(dto);
    }

    public void authenticate(AuthUserDto dto) {
        authService.authenticate(dto);
    }

}
