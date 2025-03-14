package ru.extoozy.service.profile;

import ru.extoozy.dto.profile.CreateUserProfileDto;
import ru.extoozy.dto.profile.UpdateUserProfileDto;
import ru.extoozy.dto.profile.UserProfileDto;

import java.util.List;

/**
 * Интерфейс для сервиса управления профилями пользователей.
 * Содержит методы для создания, обновления, получения, удаления профилей пользователей.
 */
public interface UserProfileService {

    /**
     * Создает новый профиль пользователя.
     *
     * @param dto объект, содержащий данные для создания профиля пользователя
     */
    void create(CreateUserProfileDto dto);

    /**
     * Обновляет существующий профиль пользователя.
     *
     * @param dto объект, содержащий данные для обновления профиля пользователя
     */
    void update(UpdateUserProfileDto dto);

    /**
     * Получает профиль пользователя по его идентификатору.
     *
     * @param id идентификатор профиля пользователя
     * @return объект {@link UserProfileDto}, представляющий профиль пользователя
     */
    UserProfileDto get(Long id);

    /**
     * Получает профиль пользователя по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return объект {@link UserProfileDto}, представляющий профиль пользователя
     */
    UserProfileDto getByUserId(Long userId);

    /**
     * Получает все профили пользователей.
     *
     * @return список объектов {@link UserProfileDto}, представляющих профили всех пользователей
     */
    List<UserProfileDto> getAll();

    /**
     * Удаляет профиль пользователя по его идентификатору.
     *
     * @param id идентификатор профиля пользователя, который нужно удалить
     */
    void delete(Long id);
}
