package ru.extoozy.service.budget;

import ru.extoozy.dto.budget.BudgetDto;
import ru.extoozy.dto.budget.CreateBudgetDto;
import ru.extoozy.dto.budget.UpdateBudgetDto;

/**
 * Интерфейс для сервиса, управляющего бюджетами пользователей.
 * Содержит методы для создания, обновления и получения информации о бюджете.
 */
public interface BudgetService {

    /**
     * Создает новый бюджет для пользователя.
     *
     * @param dto объект, содержащий данные для создания бюджета
     */
    void create(CreateBudgetDto dto);

    /**
     * Обновляет существующий бюджет для пользователя.
     *
     * @param dto объект, содержащий данные для обновления бюджета
     */
    void update(UpdateBudgetDto dto);

    /**
     * Получает текущий бюджет пользователя для заданного месяца.
     *
     * @param userProfileId идентификатор профиля пользователя
     * @return объект {@link BudgetDto}, представляющий текущий бюджет пользователя
     */
    BudgetDto getByUserProfileIdAndCurrentMonth(Long userProfileId);
}
