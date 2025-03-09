package ru.extoozy.repository.user.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.exception.ResourceNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemoryUserRepositoryTest {

    private MemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new MemoryUserRepository();
    }

    @Test
    @DisplayName("Сохранение пользователя - должен добавляться в хранилище")
    void testSave_whenUserProvided_thenStored() {
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        repository.save(user);

        assertThat(repository.findAll()).hasSize(1);
        assertThat(repository.findAll().get(0).getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Обновление пользователя - существующий пользователь обновляется")
    void testUpdate_whenUserExists_thenUpdated() {
        UserEntity user = new UserEntity();
        user.setEmail("old@example.com");
        repository.save(user);

        user.setEmail("new@example.com");
        repository.update(user);

        assertThat(repository.findById(1L).getEmail()).isEqualTo("new@example.com");
    }

    @Test
    @DisplayName("Удаление пользователя - пользователь удаляется и возвращается true")
    void testDelete_whenUserExists_thenRemoved() {
        UserEntity user = new UserEntity();
        repository.save(user);

        boolean result = repository.delete(1L);

        assertThat(result).isTrue();
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Поиск всех пользователей - должен возвращать список")
    void testFindAll_whenUsersExist_thenReturnList() {
        repository.save(new UserEntity());
        repository.save(new UserEntity());

        List<UserEntity> users = repository.findAll();
        assertThat(users).hasSize(2);
    }

    @Test
    @DisplayName("Поиск по ID - должен вернуть найденного пользователя")
    void testFindById_whenUserExists_thenReturnUser() {
        UserEntity user = new UserEntity();
        repository.save(user);

        UserEntity foundUser = repository.findById(1L);
        assertThat(foundUser).isNotNull();
    }

    @Test
    @DisplayName("Поиск по ID - выбрасывает исключение, если не найдено")
    void testFindById_whenUserNotFound_thenThrowException() {
        assertThatThrownBy(() -> repository.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with id=1 not found");
    }

    @Test
    @DisplayName("Поиск по email - должен вернуть найденного пользователя")
    void testGetByEmail_whenUserExists_thenReturnUser() {
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        repository.save(user);

        UserEntity foundUser = repository.getByEmail("test@example.com");
        assertThat(foundUser).isNotNull();
    }

    @Test
    @DisplayName("Поиск по email - выбрасывает исключение, если не найдено")
    void testGetByEmail_whenUserNotFound_thenThrowException() {
        assertThatThrownBy(() -> repository.getByEmail("missing@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with email=missing@example.com not found");
    }

    @Test
    @DisplayName("Смена статуса блокировки - должен переключаться")
    void testChangeBlockStatus_whenUserExists_thenToggleBlocked() {
        UserEntity user = new UserEntity();
        repository.save(user);
        assertThat(user.isBlocked()).isFalse();

        repository.changeBlockStatus(1L);
        assertThat(user.isBlocked()).isTrue();

        repository.changeBlockStatus(1L);
        assertThat(user.isBlocked()).isFalse();
    }
}
