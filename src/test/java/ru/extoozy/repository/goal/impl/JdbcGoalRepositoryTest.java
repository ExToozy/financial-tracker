package ru.extoozy.repository.goal.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.extoozy.config.DbConfig;
import ru.extoozy.entity.GoalEntity;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.enums.UserRole;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.migration.MigrationTool;
import ru.extoozy.repository.goal.GoalRepository;
import ru.extoozy.repository.profile.impl.JdbcUserProfileRepository;
import ru.extoozy.repository.user.impl.JdbcUserRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JdbcGoalRepositoryTest {

    private static Connection connection;
    private GoalRepository repository;

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
        UserProfileEntity userProfile = UserProfileEntity.builder()
                .firstName("first")
                .lastName("last")
                .user(user)
                .build();

        new JdbcUserRepository().save(user);
        new JdbcUserProfileRepository().save(userProfile);
    }

    @AfterAll
    static void tearDown() throws SQLException {
        connection.close();
    }

    @BeforeEach
    void setUp() {
        repository = new JdbcGoalRepository();

    }

    @AfterEach
    void deleteGoals() throws SQLException {
        connection.createStatement().execute("DELETE FROM financial_tracker_schema.goals");
    }

    @Test
    @DisplayName("Сохранение цели - должен добавляться в хранилище")
    void testSave_whenGoalProvided_thenStored() {
        GoalEntity goal = new GoalEntity();
        goal.setUserProfile(UserProfileEntity.builder().id(1L).build());
        goal.setName("Новая цель");
        goal.setGoalAmount(BigDecimal.valueOf(1000L));
        repository.save(goal);

        assertThat(repository.findAll()).hasSize(1);
        assertThat(repository.findAll().get(0).getName()).isEqualTo("Новая цель");
    }

    @Test
    @DisplayName("Обновление цели - существующая цель обновляется")
    void testUpdate_whenGoalExists_thenUpdated() {
        GoalEntity goal = new GoalEntity();
        goal.setUserProfile(UserProfileEntity.builder().id(1L).build());
        goal.setName("Старая цель");
        goal.setGoalAmount(BigDecimal.valueOf(1000L));
        repository.save(goal);

        goal.setName("Обновленная цель");
        repository.update(goal);

        assertThat(repository.findById(goal.getId()).getName()).isEqualTo("Обновленная цель");
    }

    @Test
    @DisplayName("Удаление цели - цель удаляется и возвращается true")
    void testDelete_whenGoalExists_thenRemoved() {
        GoalEntity goal = new GoalEntity();
        goal.setUserProfile(UserProfileEntity.builder().id(1L).build());
        goal.setName("test name");
        goal.setGoalAmount(BigDecimal.valueOf(1000L));
        repository.save(goal);

        boolean result = repository.delete(1L);

        assertThat(result).isTrue();
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Поиск всех целей - должен возвращать список")
    void testFindAll_whenGoalsExist_thenReturnList() {
        var goal1 = new GoalEntity();
        goal1.setName("test name");
        goal1.setUserProfile(UserProfileEntity.builder().id(1L).build());
        goal1.setGoalAmount(BigDecimal.valueOf(1000L));
        var goal2 = new GoalEntity();
        goal2.setName("test name");
        goal2.setUserProfile(UserProfileEntity.builder().id(1L).build());
        goal2.setGoalAmount(BigDecimal.valueOf(1000L));

        repository.save(goal1);
        repository.save(goal2);

        List<GoalEntity> goals = repository.findAll();
        assertThat(goals).hasSize(2);
    }

    @Test
    @DisplayName("Поиск по ID - должен вернуть найденную цель")
    void testFindById_whenGoalExists_thenReturnGoal() {
        var goal = new GoalEntity();
        goal.setName("test name");
        goal.setUserProfile(UserProfileEntity.builder().id(1L).build());
        goal.setGoalAmount(BigDecimal.valueOf(1000L));
        repository.save(goal);

        GoalEntity foundGoal = repository.findById(goal.getId());
        assertThat(foundGoal).isNotNull();
    }

    @Test
    @DisplayName("Поиск по ID - выбрасывает исключение, если не найдено")
    void testFindById_whenGoalNotFound_thenThrowException() {
        assertThatThrownBy(() -> repository.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Goal with id=1 not found");
    }

    @Test
    @DisplayName("Поиск по ID профиля пользователя - должен вернуть список целей")
    void testFindAllByUserProfileId_whenGoalsExist_thenReturnList() {
        UserProfileEntity user = new UserProfileEntity();
        user.setId(1L);

        GoalEntity goal1 = new GoalEntity();
        goal1.setUserProfile(user);
        goal1.setName("test name");
        goal1.setGoalAmount(BigDecimal.valueOf(1000L));
        repository.save(goal1);

        GoalEntity goal2 = new GoalEntity();
        goal2.setUserProfile(user);
        goal2.setName("test name");
        goal2.setGoalAmount(BigDecimal.valueOf(1000L));
        repository.save(goal2);

        List<GoalEntity> goals = repository.findAllByUserProfileId(1L);
        assertThat(goals).hasSize(2);
    }
}