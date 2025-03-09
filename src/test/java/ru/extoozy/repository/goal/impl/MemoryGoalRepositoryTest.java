package ru.extoozy.repository.goal.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.extoozy.entity.GoalEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.exception.ResourceNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemoryGoalRepositoryTest {

    private MemoryGoalRepository repository;

    @BeforeEach
    void setUp() {
        repository = new MemoryGoalRepository();
    }

    @Test
    @DisplayName("Сохранение цели - должен добавляться в хранилище")
    void testSave_whenGoalProvided_thenStored() {
        GoalEntity goal = new GoalEntity();
        goal.setName("Новая цель");
        repository.save(goal);

        assertThat(repository.findAll()).hasSize(1);
        assertThat(repository.findAll().get(0).getName()).isEqualTo("Новая цель");
    }

    @Test
    @DisplayName("Обновление цели - существующая цель обновляется")
    void testUpdate_whenGoalExists_thenUpdated() {
        GoalEntity goal = new GoalEntity();
        goal.setName("Старая цель");
        repository.save(goal);

        goal.setName("Обновленная цель");
        repository.update(goal);

        assertThat(repository.findById(1L).getName()).isEqualTo("Обновленная цель");
    }

    @Test
    @DisplayName("Удаление цели - цель удаляется и возвращается true")
    void testDelete_whenGoalExists_thenRemoved() {
        GoalEntity goal = new GoalEntity();
        repository.save(goal);

        boolean result = repository.delete(1L);

        assertThat(result).isTrue();
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Поиск всех целей - должен возвращать список")
    void testFindAll_whenGoalsExist_thenReturnList() {
        repository.save(new GoalEntity());
        repository.save(new GoalEntity());

        List<GoalEntity> goals = repository.findAll();
        assertThat(goals).hasSize(2);
    }

    @Test
    @DisplayName("Поиск по ID - должен вернуть найденную цель")
    void testFindById_whenGoalExists_thenReturnGoal() {
        GoalEntity goal = new GoalEntity();
        repository.save(goal);

        GoalEntity foundGoal = repository.findById(1L);
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
        user.setId(100L);

        GoalEntity goal1 = new GoalEntity();
        goal1.setUserProfile(user);
        repository.save(goal1);

        GoalEntity goal2 = new GoalEntity();
        goal2.setUserProfile(user);
        repository.save(goal2);

        List<GoalEntity> goals = repository.findAllByUserProfileId(100L);
        assertThat(goals).hasSize(2);
    }
}