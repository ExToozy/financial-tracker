package ru.extoozy.service.auth;

import ru.extoozy.dto.user.AuthUserDto;

public interface AuthService {
    void register(AuthUserDto dto);

    void authenticate(AuthUserDto dto);

}
