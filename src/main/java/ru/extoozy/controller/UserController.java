package ru.extoozy.controller;

import lombok.RequiredArgsConstructor;
import ru.extoozy.dto.user.UpdateUserDto;
import ru.extoozy.dto.user.UserDto;
import ru.extoozy.service.user.UserService;

import java.util.List;

/**
 * Контроллер для управления пользователями.
 * Содержит методы для обновления данных пользователя, получения списка пользователей,
 * получения пользователя по идентификатору, удаления пользователя и изменения его блокировки.
 */
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Обновляет данные пользователя.
     *
     * @param dto объект, содержащий обновленные данные пользователя
     */
    public void update(UpdateUserDto dto) {
        userService.update(dto);
    }

    /**
     * Получает список всех пользователей.
     *
     * @return список объектов {@link UserDto}, представляющих всех пользователей
     */
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return объект {@link UserDto}, представляющий пользователя
     */
    public UserDto getById(Long id) {
        return userService.get(id);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя, которого нужно удалить
     */
    public void delete(Long id) {
        userService.delete(id);
    }

    /**
     * Изменяет статус блокировки пользователя.
     *
     * @param id идентификатор пользователя, для которого нужно изменить статус блокировки
     */
    public void changeBlockStatus(Long id) {
        userService.changeBlockStatus(id);
    }
}
