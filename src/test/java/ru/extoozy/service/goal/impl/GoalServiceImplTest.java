package ru.extoozy.service.goal.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.extoozy.context.UserContext;
import ru.extoozy.dto.goal.CreateGoalDto;
import ru.extoozy.dto.goal.GoalDto;
import ru.extoozy.entity.GoalEntity;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.enums.UserRole;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.repository.goal.GoalRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class GoalServiceImplTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalServiceImpl goalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UserEntity user = new UserEntity();
        user.setRole(UserRole.ADMIN);
        user.setUserProfile(new UserProfileEntity());
        UserContext.setUser(user);
    }

    @Test
    @DisplayName("Создание цели - должна сохраняться в репозиторий")
    void testCreate_whenValidDto_thenGoalSaved() {
        CreateGoalDto dto = new CreateGoalDto();
        dto.setGoalAmount(BigDecimal.valueOf(100));
        dto.setName("test");
        goalService.create(dto);

        verify(goalRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Получение всех целей по userProfileId - должен возвращать список целей")
    void testGetAllByUserProfileId_whenGoalsExist_thenReturnList() {
        Long userProfileId = 1L;
        GoalEntity goal1 = GoalEntity.builder()
                .id(1L)
                .goalAmount(BigDecimal.valueOf(100))
                .name("test")
                .currentAmount(BigDecimal.ZERO)
                .build();
        GoalEntity goal2 = GoalEntity.builder()
                .id(2L)
                .goalAmount(BigDecimal.valueOf(100))
                .name("test2")
                .currentAmount(BigDecimal.ZERO)
                .build();
        List<GoalEntity> goals = List.of(goal1, goal2);
        when(goalRepository.findAllByUserProfileId(userProfileId)).thenReturn(goals);

        List<GoalDto> result = goalService.getAllByUserProfileId(userProfileId);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Удаление цели - если существует, должна удалиться")
    void testDelete_whenGoalExists_thenDeleted() {
        Long goalId = 1L;
        when(goalRepository.delete(goalId)).thenReturn(true);

        goalService.delete(goalId);

        verify(goalRepository, times(1)).delete(goalId);
    }

    @Test
    @DisplayName("Удаление цели - если не найдена, должно выбрасываться исключение")
    void testDelete_whenGoalNotFound_thenThrowException() {
        Long goalId = 1L;
        when(goalRepository.delete(goalId)).thenReturn(false);

        assertThatThrownBy(() -> goalService.delete(goalId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Goal with id=" + goalId + " not found");
    }

    @Test
    @DisplayName("Пополнение цели - сумма должна увеличиваться")
    void testReplenish_whenValidAmount_thenIncreaseCurrentAmount() {
        Long goalId = 1L;
        BigDecimal amount = new BigDecimal("100.00");
        GoalEntity goal = new GoalEntity();
        goal.setCurrentAmount(new BigDecimal("50.00"));
        when(goalRepository.findById(goalId)).thenReturn(goal);

        goalService.replenish(goalId, amount);

        assertThat(goal.getCurrentAmount()).isEqualByComparingTo(new BigDecimal("150.00"));
        verify(goalRepository, times(1)).update(goal);
    }
}