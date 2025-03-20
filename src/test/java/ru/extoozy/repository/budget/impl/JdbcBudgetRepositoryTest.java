package ru.extoozy.repository.budget.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.extoozy.config.DbConfig;
import ru.extoozy.entity.BudgetEntity;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.enums.UserRole;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.migration.MigrationTool;
import ru.extoozy.repository.budget.BudgetRepository;
import ru.extoozy.repository.profile.impl.JdbcUserProfileRepository;
import ru.extoozy.repository.user.impl.JdbcUserRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JdbcBudgetRepositoryTest {

    private static Connection connection;
    private BudgetRepository repository;

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
        repository = new JdbcBudgetRepository();

    }

    @AfterEach
    void deleteBudgets() throws SQLException {
        connection.createStatement().execute("DELETE FROM financial_tracker_schema.budgets");
    }

    @Test
    @DisplayName("Сохранение бюджета - должен присвоить ID и сохранить объект")
    void testSave_whenValidBudget_thenAssignedIdAndSaved() {
        BudgetEntity budget = new BudgetEntity();
        budget.setCurrentAmount(BigDecimal.valueOf(100));
        budget.setMaxAmount(BigDecimal.valueOf(500));
        budget.setPeriod(YearMonth.of(2024, 3));
        budget.setUserProfile(UserProfileEntity.builder().id(1L).build());

        repository.save(budget);

        assertThat(budget.getId()).isNotNull();
        assertThat(repository.findById(budget.getId()).getCurrentAmount()).isEqualTo(budget.getCurrentAmount());
    }

    @Test
    @DisplayName("Обновление бюджета - должен обновить существующий объект")
    void testUpdate_whenExistingBudget_thenUpdatedFields() {
        BudgetEntity budget = new BudgetEntity();
        budget.setCurrentAmount(BigDecimal.valueOf(100));
        budget.setMaxAmount(BigDecimal.valueOf(500));
        budget.setPeriod(YearMonth.of(2024, 3));
        budget.setUserProfile(UserProfileEntity.builder().id(1L).build());
        repository.save(budget);

        BudgetEntity updatedBudget = new BudgetEntity();
        updatedBudget.setId(budget.getId());
        updatedBudget.setCurrentAmount(BigDecimal.valueOf(200));
        updatedBudget.setMaxAmount(BigDecimal.valueOf(600));
        repository.update(updatedBudget);

        BudgetEntity foundBudget = repository.findById(budget.getId());
        assertThat(foundBudget.getCurrentAmount()).isEqualTo(BigDecimal.valueOf(200));
        assertThat(foundBudget.getMaxAmount()).isEqualTo(BigDecimal.valueOf(600));
    }

    @Test
    @DisplayName("Удаление бюджета - должен удалить существующий объект")
    void testDelete_whenExistingBudget_thenRemoved() {
        BudgetEntity budget = new BudgetEntity();
        budget.setMaxAmount(BigDecimal.valueOf(500));
        budget.setCurrentAmount(BigDecimal.valueOf(100));
        budget.setPeriod(YearMonth.of(2024, 3));
        budget.setUserProfile(UserProfileEntity.builder().id(1L).build());
        repository.save(budget);

        boolean deleted = repository.delete(budget.getId());

        assertThat(deleted).isTrue();
        assertThatThrownBy(() -> repository.findById(budget.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Поиск всех бюджетов - должен вернуть список")
    void testFindAll_whenMultipleBudgets_thenReturnList() {
        BudgetEntity budget1 = new BudgetEntity();
        budget1.setCurrentAmount(BigDecimal.valueOf(100));
        budget1.setMaxAmount(BigDecimal.valueOf(500));
        budget1.setPeriod(YearMonth.of(2024, 3));
        budget1.setUserProfile(UserProfileEntity.builder().id(1L).build());

        BudgetEntity budget2 = new BudgetEntity();
        budget2.setCurrentAmount(BigDecimal.valueOf(100));
        budget2.setMaxAmount(BigDecimal.valueOf(500));
        budget2.setPeriod(YearMonth.of(2024, 3));
        budget2.setUserProfile(UserProfileEntity.builder().id(1L).build());

        repository.save(budget1);
        repository.save(budget2);

        List<BudgetEntity> budgets = repository.findAll();

        assertThat(budgets).hasSize(2);
    }

    @Test
    @DisplayName("Поиск по ID - должен выбросить исключение, если не найдено")
    void testFindById_whenNotFound_thenThrowException() {
        assertThatThrownBy(() -> repository.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Поиск по ID пользователя - должен вернуть список его бюджетов")
    void testFindAllByUserProfileId_whenValidUser_thenReturnBudgets() {
        UserProfileEntity user = UserProfileEntity.builder().id(1L).build();
        user.setId(1L);

        BudgetEntity budget1 = new BudgetEntity();
        budget1.setCurrentAmount(BigDecimal.valueOf(100));
        budget1.setMaxAmount(BigDecimal.valueOf(500));
        budget1.setUserProfile(user);
        budget1.setPeriod(YearMonth.of(2024, 3));
        repository.save(budget1);

        BudgetEntity budget2 = new BudgetEntity();
        budget2.setCurrentAmount(BigDecimal.valueOf(100));
        budget2.setMaxAmount(BigDecimal.valueOf(500));
        budget2.setUserProfile(user);
        budget2.setPeriod(YearMonth.of(2024, 3));
        repository.save(budget2);

        List<BudgetEntity> budgets = repository.findAllByUserProfileId(1L);

        assertThat(budgets).hasSize(2);
    }

    @Test
    @DisplayName("Поиск бюджета по ID пользователя и текущему месяцу - должен вернуть объект")
    void testFindByUserProfileIdAndCurrentMonth_whenExists_thenReturnBudget() {
        UserProfileEntity user = UserProfileEntity.builder().id(1L).build();
        user.setId(1L);

        BudgetEntity budget = new BudgetEntity();
        budget.setUserProfile(user);
        budget.setCurrentAmount(BigDecimal.valueOf(100));
        budget.setMaxAmount(BigDecimal.valueOf(500));
        budget.setPeriod(YearMonth.now());
        repository.save(budget);

        BudgetEntity found = repository.findByUserProfileIdAndCurrentMonth(1L);
        assertThat(found.getCurrentAmount()).isEqualTo(budget.getCurrentAmount());
    }

    @Test
    @DisplayName("Поиск бюджета по ID пользователя и текущему месяцу - должен выбросить исключение, если не найдено")
    void testFindByUserProfileIdAndCurrentMonth_whenNotFound_thenThrowException() {
        assertThatThrownBy(() -> repository.findByUserProfileIdAndCurrentMonth(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }
}
