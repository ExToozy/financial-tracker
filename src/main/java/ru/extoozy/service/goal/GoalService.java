package ru.extoozy.service.goal;

import ru.extoozy.dto.goal.CreateGoalDto;
import ru.extoozy.dto.goal.GoalDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Интерфейс для сервиса управления целями пользователей.
 * Содержит методы для создания, получения, удаления и пополнения целей.
 */
public interface GoalService {

    /**
     * Создает новую цель для пользователя.
     *
     * @param dto объект, содержащий данные для создания цели
     */
    void create(CreateGoalDto dto);

    /**
     * Получает список всех целей пользователя по его идентификатору профиля.
     *
     * @param id идентификатор профиля пользователя
     * @return список объектов {@link GoalDto}, представляющих цели пользователя
     */
    List<GoalDto> getAllByUserProfileId(Long id);

    /**
     * Удаляет цель по ее идентификатору.
     *
     * @param id идентификатор цели, которую нужно удалить
     */
    void delete(Long id);

    /**
     * Пополняет цель на указанную сумму.
     *
     * @param goalId идентификатор цели, которую нужно пополнить
     * @param amount сумма, на которую нужно пополнить цель
     */
    void replenish(Long goalId, BigDecimal amount);
}
