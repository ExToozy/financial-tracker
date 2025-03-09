package ru.extoozy.controller;

import lombok.RequiredArgsConstructor;
import ru.extoozy.dto.user.UpdateUserDto;
import ru.extoozy.dto.user.UserDto;
import ru.extoozy.service.user.UserService;

import java.util.List;

@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    public void update(UpdateUserDto dto) {
        userService.update(dto);
    }

    public List<UserDto> getAll() {
        return userService.getAll();
    }

    public UserDto getById(Long id) {
        return userService.get(id);
    }

    public void delete(Long id) {
        userService.delete(id);
    }

    public void changeBlockStatus(Long id) {
        userService.changeBlockStatus(id);
    }
}
