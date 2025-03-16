package ru.extoozy.repository.profile.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.extoozy.config.DbConfig;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.enums.UserRole;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.migration.MigrationTool;
import ru.extoozy.repository.profile.UserProfileRepository;
import ru.extoozy.repository.user.impl.JdbcUserRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JdbcUserProfileRepositoryTest {

    private static Connection connection;
    private UserProfileRepository repository;

    @BeforeAll
    static void runMigration() throws SQLException {
        DbConfig dbConfig = new DbConfig();
        connection = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getUsername());
        MigrationTool.runMigrate();
        var user = UserEntity.builder()
                .role(UserRole.USER)
                .password("test")
                .email("test")
                .build();

        new JdbcUserRepository().save(user);
    }

    @AfterAll
    static void tearDown() throws SQLException {
        connection.close();
    }


    @BeforeEach
    void setUp() {
        repository = new JdbcUserProfileRepository();

    }

    @AfterEach
    void deleteUserProfiles() throws SQLException {
        connection.createStatement().execute("DELETE FROM financial_tracker_schema.user_profiles");
    }

    @Test
    @DisplayName("Сохранение профиля пользователя - должен добавляться в хранилище")
    void testSave_whenUserProfileProvided_thenStored() {
        UserProfileEntity profile = new UserProfileEntity();
        profile.setUser(UserEntity.builder().id(1L).build());
        profile.setFirstName("Иван");
        profile.setLastName("Иванов");
        profile.setUser(UserEntity.builder().id(1L).build());
        repository.save(profile);

        assertThat(repository.findAll()).hasSize(1);
        assertThat(repository.findAll().get(0).getFirstName()).isEqualTo("Иван");
    }

    @Test
    @DisplayName("Обновление профиля пользователя - существующий профиль обновляется")
    void testUpdate_whenUserProfileExists_thenUpdated() {
        UserProfileEntity profile = new UserProfileEntity();
        profile.setUser(UserEntity.builder().id(1L).build());
        profile.setLastName("Иванов");
        profile.setFirstName("Старое имя");
        profile.setUser(UserEntity.builder().id(1L).build());
        repository.save(profile);

        profile.setFirstName("Новое имя");
        repository.update(profile);

        assertThat(repository.findById(profile.getId()).getFirstName()).isEqualTo("Новое имя");
        assertThat(repository.findById(profile.getId()).getLastName()).isEqualTo("Иванов");
    }

    @Test
    @DisplayName("Удаление профиля пользователя - профиль удаляется и возвращается true")
    void testDelete_whenUserProfileExists_thenRemoved() {
        UserProfileEntity profile = new UserProfileEntity();
        profile.setFirstName("Иван");
        profile.setLastName("Иванов");
        profile.setUser(UserEntity.builder().id(1L).build());
        repository.save(profile);

        boolean result = repository.delete(profile.getId());

        assertThat(result).isTrue();
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Поиск всех профилей - должен возвращать список")
    void testFindAll_whenUserProfilesExist_thenReturnList() {

        UserProfileEntity profile1 = new UserProfileEntity();
        profile1.setFirstName("Иван1");
        profile1.setLastName("Иванов1");
        profile1.setUser(UserEntity.builder().id(1L).build());

        UserProfileEntity profile2 = new UserProfileEntity();
        profile2.setFirstName("Иван2");
        profile2.setLastName("Иванов2");
        profile2.setUser(UserEntity.builder().id(1L).build());

        repository.save(profile1);
        repository.save(profile2);

        List<UserProfileEntity> profiles = repository.findAll();
        assertThat(profiles).hasSize(2);
    }

    @Test
    @DisplayName("Поиск по ID - должен вернуть найденный профиль пользователя")
    void testFindById_whenUserProfileExists_thenReturnUserProfile() {
        UserProfileEntity profile = new UserProfileEntity();
        profile.setFirstName("Иван");
        profile.setLastName("Иванов");
        profile.setUser(UserEntity.builder().id(1L).build());
        repository.save(profile);

        UserProfileEntity foundProfile = repository.findById(profile.getId());
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
        user.setId(1L);

        UserProfileEntity profile = new UserProfileEntity();
        profile.setFirstName("Иван");
        profile.setLastName("Иванов");
        profile.setUser(user);
        repository.save(profile);

        UserProfileEntity foundProfile = repository.findByUserId(1L);
        assertThat(foundProfile).isNotNull();
    }
}
