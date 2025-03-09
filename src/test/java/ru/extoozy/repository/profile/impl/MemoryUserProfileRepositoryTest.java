package ru.extoozy.repository.profile.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.exception.ResourceNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemoryUserProfileRepositoryTest {

    private MemoryUserProfileRepository repository;

    @BeforeEach
    void setUp() {
        repository = new MemoryUserProfileRepository();
    }

    @Test
    @DisplayName("Сохранение профиля пользователя - должен добавляться в хранилище")
    void testSave_whenUserProfileProvided_thenStored() {
        UserProfileEntity profile = new UserProfileEntity();
        profile.setFirstName("Иван");
        repository.save(profile);

        assertThat(repository.findAll()).hasSize(1);
        assertThat(repository.findAll().get(0).getFirstName()).isEqualTo("Иван");
    }

    @Test
    @DisplayName("Обновление профиля пользователя - существующий профиль обновляется")
    void testUpdate_whenUserProfileExists_thenUpdated() {
        UserProfileEntity profile = new UserProfileEntity();
        profile.setFirstName("Старое имя");
        repository.save(profile);

        profile.setFirstName("Новое имя");
        repository.update(profile);

        assertThat(repository.findById(1L).getFirstName()).isEqualTo("Новое имя");
    }

    @Test
    @DisplayName("Удаление профиля пользователя - профиль удаляется и возвращается true")
    void testDelete_whenUserProfileExists_thenRemoved() {
        UserProfileEntity profile = new UserProfileEntity();
        repository.save(profile);

        boolean result = repository.delete(1L);

        assertThat(result).isTrue();
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Поиск всех профилей - должен возвращать список")
    void testFindAll_whenUserProfilesExist_thenReturnList() {
        repository.save(new UserProfileEntity());
        repository.save(new UserProfileEntity());

        List<UserProfileEntity> profiles = repository.findAll();
        assertThat(profiles).hasSize(2);
    }

    @Test
    @DisplayName("Поиск по ID - должен вернуть найденный профиль пользователя")
    void testFindById_whenUserProfileExists_thenReturnUserProfile() {
        UserProfileEntity profile = new UserProfileEntity();
        repository.save(profile);

        UserProfileEntity foundProfile = repository.findById(1L);
        assertThat(foundProfile).isNotNull();
    }

    @Test
    @DisplayName("Поиск по ID - выбрасывает исключение, если профиль не найден")
    void testFindById_whenUserProfileNotFound_thenThrowException() {
        assertThatThrownBy(() -> repository.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User profile with id=1 not found");
    }

    @Test
    @DisplayName("Поиск по ID пользователя - должен вернуть профиль пользователя")
    void testFindByUserId_whenUserProfileExists_thenReturnUserProfile() {
        UserEntity user = new UserEntity();
        user.setId(100L);

        UserProfileEntity profile = new UserProfileEntity();
        profile.setUser(user);
        repository.save(profile);

        UserProfileEntity foundProfile = repository.findByUserId(100L);
        assertThat(foundProfile).isNotNull();
    }
}
