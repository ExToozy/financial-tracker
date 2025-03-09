package ru.extoozy.service.user;

import ru.extoozy.dto.user.AuthUserDto;
import ru.extoozy.dto.user.UpdateUserDto;
import ru.extoozy.dto.user.UserDto;

import java.util.List;

public interface UserService {
    void create(AuthUserDto dto);

    void update(UpdateUserDto dto);

    UserDto get(Long id);

    List<UserDto> getAll();

    void delete(Long id);

    boolean containsByEmail(String email);


    void changeBlockStatus(Long id);
}
