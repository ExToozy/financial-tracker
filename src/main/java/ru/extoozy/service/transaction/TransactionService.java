package ru.extoozy.service.transaction;

import ru.extoozy.dto.transaction.CreateTransactionDto;
import ru.extoozy.dto.transaction.TransactionDto;
import ru.extoozy.dto.transaction.UpdateTransactionDto;

import java.util.List;

public interface TransactionService {
    void create(CreateTransactionDto transaction);

    void update(UpdateTransactionDto transaction);

    TransactionDto get(Long id);

    List<TransactionDto> getAllByUserProfileId(Long userId);

    void delete(Long id);

}
