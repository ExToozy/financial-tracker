package ru.extoozy.service.transaction;

import ru.extoozy.dto.transaction.CreateTransactionDto;
import ru.extoozy.dto.transaction.TransactionDto;
import ru.extoozy.dto.transaction.UpdateTransactionDto;

import java.util.List;

/**
 * Интерфейс для сервиса управления транзакциями пользователей.
 * Содержит методы для создания, обновления, получения, удаления транзакций.
 */
public interface TransactionService {

    /**
     * Создает новую транзакцию.
     *
     * @param transaction объект, содержащий данные для создания транзакции
     */
    void create(CreateTransactionDto transaction);

    /**
     * Обновляет существующую транзакцию.
     *
     * @param transaction объект, содержащий данные для обновления транзакции
     */
    void update(UpdateTransactionDto transaction);

    /**
     * Получает транзакцию по ее идентификатору.
     *
     * @param id идентификатор транзакции
     * @return объект {@link TransactionDto}, представляющий транзакцию
     */
    TransactionDto get(Long id);

    /**
     * Получает все транзакции пользователя по его идентификатору профиля.
     *
     * @param userId идентификатор профиля пользователя
     * @return список объектов {@link TransactionDto}, представляющих транзакции пользователя
     */
    List<TransactionDto> getAllByUserProfileId(Long userId);

    /**
     * Удаляет транзакцию по ее идентификатору.
     *
     * @param id идентификатор транзакции, которую нужно удалить
     */
    void delete(Long id);
}
