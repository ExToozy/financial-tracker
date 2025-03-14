package ru.extoozy.service.user;

import ru.extoozy.dto.user.AuthUserDto;
import ru.extoozy.dto.user.UpdateUserDto;
import ru.extoozy.dto.user.UserDto;

import java.util.List;

/**
 * Интерфейс для сервиса управления пользователями.
 * Содержит методы для создания, обновления, получения, удаления пользователей,
 * а также для проверки и изменения статуса блокировки пользователя.
 */
public interface UserService {

    /**
     * Создает нового пользователя.
     *
     * @param dto объект, содержащий данные для создания пользователя
     */
    void create(AuthUserDto dto);

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param dto объект, содержащий данные для обновления пользователя
     */
    void update(UpdateUserDto dto);

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return объект {@link UserDto}, представляющий пользователя
     */
    UserDto get(Long id);

    /**
     * Получает список всех пользователей.
     *
     * @return список объектов {@link UserDto}, представляющих всех пользователей
     */
    List<UserDto> getAll();

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя, которого нужно удалить
     */
    void delete(Long id);

    /**
     * Проверяет, существует ли пользователь с указанным адресом электронной почты.
     *
     * @param email адрес электронной почты пользователя
     * @return {@code true}, если пользователь с таким email существует, иначе {@code false}
     */
    boolean containsByEmail(String email);

    /**
     * Меняет статус блокировки пользователя.
     *
     * @param id идентификатор пользователя, для которого нужно изменить статус блокировки
     */
    void changeBlockStatus(Long id);
}
