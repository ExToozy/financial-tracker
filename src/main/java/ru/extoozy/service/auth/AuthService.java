package ru.extoozy.service.auth;

import ru.extoozy.dto.user.AuthUserDto;

public interface AuthService {
    void register(AuthUserDto dto);

    String authenticate(AuthUserDto dto);

}
