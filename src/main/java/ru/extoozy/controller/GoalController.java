package ru.extoozy.controller;

import lombok.RequiredArgsConstructor;
import ru.extoozy.dto.goal.CreateGoalDto;
import ru.extoozy.dto.goal.GoalDto;
import ru.extoozy.service.goal.GoalService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Контроллер для управления целями пользователя.
 * Содержит методы для создания, получения, удаления и пополнения целей.
 */
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    /**
     * Создает новую цель для пользователя.
     *
     * @param dto объект, содержащий данные для создания цели
     */
    public void create(CreateGoalDto dto) {
        goalService.create(dto);
    }

    /**
     * Получает все цели пользователя по идентификатору профиля.
     *
     * @param id идентификатор профиля пользователя
     * @return список объектов {@link GoalDto}, представляющих цели пользователя
     */
    public List<GoalDto> getAllByUserProfileId(Long id) {
        return goalService.getAllByUserProfileId(id);
    }

    /**
     * Удаляет цель по идентификатору.
     *
     * @param id идентификатор цели, которую нужно удалить
     */
    public void delete(Long id) {
        goalService.delete(id);
    }

    /**
     * Пополняет цель на указанную сумму.
     *
     * @param goalId идентификатор цели, которую нужно пополнить
     * @param amount сумма, на которую нужно пополнить цель
     */
    public void replenish(Long goalId, BigDecimal amount) {
        goalService.replenish(goalId, amount);
    }
}
