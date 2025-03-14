package ru.extoozy.controller;

import lombok.RequiredArgsConstructor;
import ru.extoozy.dto.transaction.CreateTransactionDto;
import ru.extoozy.dto.transaction.TransactionDto;
import ru.extoozy.dto.transaction.UpdateTransactionDto;
import ru.extoozy.service.transaction.TransactionService;

import java.util.List;

/**
 * Контроллер для управления транзакциями пользователей.
 * Содержит методы для создания, обновления, получения, удаления и получения всех транзакций пользователя.
 */
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Создает новую транзакцию.
     *
     * @param dto объект, содержащий данные для создания транзакции
     */
    public void create(CreateTransactionDto dto) {
        transactionService.create(dto);
    }

    /**
     * Обновляет существующую транзакцию.
     *
     * @param dto объект, содержащий данные для обновления транзакции
     */
    public void update(UpdateTransactionDto dto) {
        transactionService.update(dto);
    }

    /**
     * Получает транзакцию по ее идентификатору.
     *
     * @param id идентификатор транзакции
     * @return объект {@link TransactionDto}, представляющий транзакцию
     */
    public TransactionDto get(Long id) {
        return transactionService.get(id);
    }

    /**
     * Удаляет транзакцию по ее идентификатору.
     *
     * @param id идентификатор транзакции, которую нужно удалить
     */
    public void delete(Long id) {
        transactionService.delete(id);
    }

    /**
     * Получает все транзакции пользователя по его идентификатору профиля.
     *
     * @param userProfileId идентификатор профиля пользователя
     * @return список объектов {@link TransactionDto}, представляющих транзакции пользователя
     */
    public List<TransactionDto> getAllByUserProfileId(Long userProfileId) {
        return transactionService.getAllByUserProfileId(userProfileId);
    }
}
