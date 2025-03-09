package ru.extoozy.repository.budget.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.extoozy.entity.BudgetEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BudgetRepositoryImplTest {

    private BudgetRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new BudgetRepositoryImpl();
    }

    @Test
    @DisplayName("Сохранение бюджета - должен присвоить ID и сохранить объект")
    void testSave_whenValidBudget_thenAssignedIdAndSaved() {
        BudgetEntity budget = new BudgetEntity();
        budget.setCurrentAmount(BigDecimal.valueOf(100));
        budget.setMaxAmount(BigDecimal.valueOf(500));

        repository.save(budget);

        assertThat(budget.getId()).isNotNull();
        assertThat(repository.findById(budget.getId())).isEqualTo(budget);
    }

    @Test
    @DisplayName("Обновление бюджета - должен обновить существующий объект")
    void testUpdate_whenExistingBudget_thenUpdatedFields() {
        BudgetEntity budget = new BudgetEntity();
        budget.setCurrentAmount(BigDecimal.valueOf(100));
        budget.setMaxAmount(BigDecimal.valueOf(500));
        repository.save(budget);

        BudgetEntity updatedBudget = new BudgetEntity();
        updatedBudget.setId(budget.getId());
        updatedBudget.setCurrentAmount(BigDecimal.valueOf(200));
        repository.update(updatedBudget);

        BudgetEntity foundBudget = repository.findById(budget.getId());
        assertThat(foundBudget.getCurrentAmount()).isEqualTo(BigDecimal.valueOf(200));
        assertThat(foundBudget.getMaxAmount()).isEqualTo(BigDecimal.valueOf(500));
    }

    @Test
    @DisplayName("Удаление бюджета - должен удалить существующий объект")
    void testDelete_whenExistingBudget_thenRemoved() {
        BudgetEntity budget = new BudgetEntity();
        repository.save(budget);

        boolean deleted = repository.delete(budget.getId());

        assertThat(deleted).isTrue();
        assertThatThrownBy(() -> repository.findById(budget.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Поиск всех бюджетов - должен вернуть список")
    void testFindAll_whenMultipleBudgets_thenReturnList() {
        repository.save(new BudgetEntity());
        repository.save(new BudgetEntity());

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
        UserProfileEntity user = new UserProfileEntity();
        user.setId(1L);

        BudgetEntity budget1 = new BudgetEntity();
        budget1.setUserProfile(user);
        repository.save(budget1);

        BudgetEntity budget2 = new BudgetEntity();
        budget2.setUserProfile(user);
        repository.save(budget2);

        List<BudgetEntity> budgets = repository.fingAllByUserProfileId(1L);

        assertThat(budgets).hasSize(2);
    }

    @Test
    @DisplayName("Поиск бюджета по ID пользователя и текущему месяцу - должен вернуть объект")
    void testFindByUserProfileIdAndCurrentMonth_whenExists_thenReturnBudget() {
        UserProfileEntity user = new UserProfileEntity();
        user.setId(1L);

        BudgetEntity budget = new BudgetEntity();
        budget.setUserProfile(user);
        budget.setPeriod(YearMonth.now());
        repository.save(budget);

        BudgetEntity found = repository.findByUserProfileIdAndCurrentMonth(1L);
        assertThat(found).isEqualTo(budget);
    }

    @Test
    @DisplayName("Поиск бюджета по ID пользователя и текущему месяцу - должен выбросить исключение, если не найдено")
    void testFindByUserProfileIdAndCurrentMonth_whenNotFound_thenThrowException() {
        assertThatThrownBy(() -> repository.findByUserProfileIdAndCurrentMonth(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }
}
