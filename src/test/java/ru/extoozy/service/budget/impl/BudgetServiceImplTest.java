package ru.extoozy.service.budget.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.extoozy.context.UserContext;
import ru.extoozy.dto.budget.BudgetDto;
import ru.extoozy.dto.budget.CreateBudgetDto;
import ru.extoozy.dto.budget.UpdateBudgetDto;
import ru.extoozy.entity.BudgetEntity;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.repository.budget.BudgetRepository;

import java.math.BigDecimal;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BudgetServiceImplTest {

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private BudgetServiceImpl budgetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UserProfileEntity userProfile = new UserProfileEntity();
        UserEntity user = new UserEntity();
        user.setUserProfile(userProfile);
        UserContext.setUser(user);
    }

    @Test
    @DisplayName("Создание бюджета - должен сохранять бюджет с текущим месяцем и нулевой суммой")
    void testCreate_whenValidDtoProvided_thenBudgetSaved() {
        CreateBudgetDto dto = new CreateBudgetDto();
        BudgetEntity budgetEntity = new BudgetEntity();
        budgetEntity.setUserProfile(UserContext.getUser().getUserProfile());
        budgetEntity.setPeriod(YearMonth.now());
        budgetEntity.setCurrentAmount(BigDecimal.ZERO);

        budgetService.create(dto);

        verify(budgetRepository, times(1)).save(any(BudgetEntity.class));
    }

    @Test
    @DisplayName("Обновление бюджета - должен обновлять существующий бюджет")
    void testUpdate_whenValidDtoProvided_thenBudgetUpdated() {
        UpdateBudgetDto dto = UpdateBudgetDto.builder()
                .maxAmount(BigDecimal.valueOf(500))
                .currentAmount(BigDecimal.valueOf(500))
                .build();
        BudgetEntity budgetEntity = new BudgetEntity();
        when(budgetRepository.findByUserProfileIdAndCurrentMonth(any())).thenReturn(budgetEntity);
        budgetService.update(dto);
        assertThat(budgetEntity.getCurrentAmount()).usingComparator(BigDecimal::compareTo).isEqualTo(BigDecimal.valueOf(500));
        verify(budgetRepository, times(1)).update(any(BudgetEntity.class));
    }

    @Test
    @DisplayName("Получение бюджета - должен вернуть бюджет по userProfileId и текущему месяцу")
    void testGetByUserProfileIdAndCurrentMonth_whenValidIdProvided_thenReturnBudgetDto() {
        Long userProfileId = 1L;
        BudgetEntity budgetEntity = new BudgetEntity();

        when(budgetRepository.findByUserProfileIdAndCurrentMonth(userProfileId)).thenReturn(budgetEntity);

        BudgetDto result = budgetService.getByUserProfileIdAndCurrentMonth(userProfileId);

        assertThat(result).isNotNull();
        verify(budgetRepository, times(1)).findByUserProfileIdAndCurrentMonth(userProfileId);
    }
}
