package ru.extoozy.repository.user.impl;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.extoozy.config.DbConfig;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.enums.UserRole;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.migration.MigrationTool;
import ru.extoozy.repository.user.UserRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JdbcUserRepositoryTest {

    private static Connection connection;
    private UserRepository repository;

    @BeforeAll
    static void runMigration() throws SQLException {
        DbConfig dbConfig = new DbConfig();
        connection = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getUsername());
        MigrationTool.runMigrate();
    }

    @AfterAll
    static void tearDown() throws SQLException {
        connection.close();
    }

    @BeforeEach
    void setUp() {
        repository = new JdbcUserRepository();
    }

    @AfterEach
    void deleteUsers() throws SQLException {
        connection.createStatement().execute("DELETE FROM financial_tracker_schema.users");
    }

    @Test
    @DisplayName("Сохранение пользователя - должен добавляться в хранилище")
    void testSave_whenUserProvided_thenStored() {
        var user = UserEntity.builder()
                .role(UserRole.USER)
                .password("test")
                .email("test@mail.com")
                .build();
        repository.save(user);

        assertThat(repository.findAll()).hasSize(1);
        assertThat(repository.findAll().get(0).getEmail()).isEqualTo("test@mail.com");
    }

    @Test
    @DisplayName("Обновление пользователя - существующий пользователь обновляется")
    void testUpdate_whenUserExists_thenUpdated() {
        var user = UserEntity.builder()
                .role(UserRole.USER)
                .password("test")
                .email("old_test@mail.ru")
                .build();
        repository.save(user);

        user.setEmail("new@example.com");
        repository.update(user);

        assertThat(repository.findById(user.getId()).getEmail()).isEqualTo("new@example.com");
    }

    @Test
    @DisplayName("Удаление пользователя - пользователь удаляется и возвращается true")
    void testDelete_whenUserExists_thenRemoved() {
        var user = UserEntity.builder()
                .role(UserRole.USER)
                .password("test")
                .email("test@mail.ru")
                .build();
        repository.save(user);

        boolean result = repository.delete(user.getId());

        assertThat(result).isTrue();
        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("Поиск всех пользователей - должен возвращать список")
    void testFindAll_whenUsersExist_thenReturnList() {

        var user1 = UserEntity.builder()
                .role(UserRole.USER)
                .password("test")
                .email("test1@mail.ru")
                .build();
        var user2 = UserEntity.builder()
                .role(UserRole.USER)
                .password("test")
                .email("test2@mail.ru")
                .build();

        repository.save(user1);
        repository.save(user2);

        List<UserEntity> users = repository.findAll();
        assertThat(users).hasSize(2);
    }

    @Test
    @DisplayName("Поиск по ID - должен вернуть найденного пользователя")
    void testFindById_whenUserExists_thenReturnUser() {
        var user = UserEntity.builder()
                .role(UserRole.USER)
                .password("test")
                .email("test@mail.ru")
                .build();
        repository.save(user);

        UserEntity foundUser = repository.findById(user.getId());
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
        var user = UserEntity.builder()
                .role(UserRole.USER)
                .password("test")
                .email("test@mail.ru")
                .build();
        repository.save(user);

        UserEntity foundUser = repository.getByEmail("test@mail.ru");
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
        var user = UserEntity.builder()
                .role(UserRole.USER)
                .password("test")
                .blocked(false)
                .email("test@mail.ru")
                .build();
        repository.save(user);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(repository.findById(user.getId()).isBlocked()).isFalse();

        repository.changeBlockStatus(user.getId());
        softly.assertThat(repository.findById(user.getId()).isBlocked()).isTrue();

        repository.changeBlockStatus(user.getId());
        softly.assertThat(repository.findById(user.getId()).isBlocked()).isFalse();

        softly.assertAll();
    }
}
