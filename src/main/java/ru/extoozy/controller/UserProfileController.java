package ru.extoozy.controller;

import lombok.RequiredArgsConstructor;
import ru.extoozy.dto.profile.CreateUserProfileDto;
import ru.extoozy.dto.profile.UpdateUserProfileDto;
import ru.extoozy.dto.profile.UserProfileDto;
import ru.extoozy.service.profile.UserProfileService;

import java.util.List;

/**
 * Контроллер для управления профилями пользователей.
 * Содержит методы для создания, обновления профиля пользователя, получения списка профилей
 * и получения профиля по идентификатору пользователя.
 */
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * Создает новый профиль пользователя.
     *
     * @param dto объект, содержащий данные для создания профиля пользователя
     */
    public void create(CreateUserProfileDto dto) {
        userProfileService.create(dto);
    }

    /**
     * Обновляет профиль пользователя.
     *
     * @param dto объект, содержащий обновленные данные профиля пользователя
     */
    public void update(UpdateUserProfileDto dto) {
        userProfileService.update(dto);
    }

    /**
     * Получает список всех профилей пользователей.
     *
     * @return список объектов {@link UserProfileDto}, представляющих профили пользователей
     */
    public List<UserProfileDto> getAll() {
        return userProfileService.getAll();
    }

    /**
     * Получает профиль пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return объект {@link UserProfileDto}, представляющий профиль пользователя
     */
    public UserProfileDto getByUserId(Long userId) {
        return userProfileService.getByUserId(userId);
    }
}
