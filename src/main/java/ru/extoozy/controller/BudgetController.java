package ru.extoozy.controller;

import lombok.RequiredArgsConstructor;
import ru.extoozy.dto.budget.BudgetDto;
import ru.extoozy.dto.budget.CreateBudgetDto;
import ru.extoozy.dto.budget.UpdateBudgetDto;
import ru.extoozy.service.budget.BudgetService;

/**
 * Контроллер для управления бюджетом пользователей.
 * Содержит методы для создания, обновления и получения информации о бюджете.
 */
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    /**
     * Создает новый бюджет для пользователя.
     *
     * @param dto объект, содержащий данные для создания бюджета
     */
    public void create(CreateBudgetDto dto) {
        budgetService.create(dto);
    }

    /**
     * Обновляет бюджет пользователя.
     *
     * @param dto объект, содержащий данные для обновления бюджета
     */
    public void update(UpdateBudgetDto dto) {
        budgetService.update(dto);
    }

    /**
     * Получает бюджет пользователя за текущий месяц по идентификатору профиля пользователя.
     *
     * @param id идентификатор профиля пользователя
     * @return объект {@link BudgetDto}, представляющий бюджет пользователя за текущий месяц
     */
    public BudgetDto getByUserProfileIdAndCurrentMonth(Long id) {
        return budgetService.getByUserProfileIdAndCurrentMonth(id);
    }
}
