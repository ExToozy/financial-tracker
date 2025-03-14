package ru.extoozy.service.auth.impl;

import lombok.RequiredArgsConstructor;
import ru.extoozy.context.UserContext;
import ru.extoozy.dto.user.AuthUserDto;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.exception.UserAlreadyExistsException;
import ru.extoozy.exception.UserIsBlockedException;
import ru.extoozy.repository.user.UserRepository;
import ru.extoozy.security.PasswordHelper;
import ru.extoozy.service.auth.AuthService;
import ru.extoozy.service.user.UserService;

@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final UserRepository userRepository;

    @Override
    public void register(AuthUserDto dto) {
        if (userService.containsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException("User with email=%s already exists".formatted(dto.getEmail()));
        }
        dto.setPassword(PasswordHelper.getPasswordHash(dto.getPassword()));
        userService.create(dto);
        UserEntity user = userRepository.getByEmail(dto.getEmail());
        UserContext.setUser(user);
    }

    @Override
    public void authenticate(AuthUserDto dto) {
        UserEntity user = userRepository.getByEmail(dto.getEmail());

        if (!PasswordHelper.checkPasswordEquals(dto.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Wrong password");
        }
        if (user.isBlocked()) {
            throw new UserIsBlockedException("User blocked");
        }

        UserContext.setUser(user);

    }
}
