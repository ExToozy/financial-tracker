package ru.extoozy.controller;

import lombok.RequiredArgsConstructor;
import ru.extoozy.dto.transaction.CreateTransactionDto;
import ru.extoozy.dto.transaction.TransactionDto;
import ru.extoozy.dto.transaction.UpdateTransactionDto;
import ru.extoozy.service.transaction.TransactionService;

import java.util.List;

@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    public void create(CreateTransactionDto dto) {
        transactionService.create(dto);
    }

    public void update(UpdateTransactionDto dto) {
        transactionService.update(dto);
    }

    public TransactionDto get(Long id) {
        return transactionService.get(id);
    }

    public void delete(Long id) {
        transactionService.delete(id);
    }

    public List<TransactionDto> getAllByUserProfileId(Long userProfileId) {
        return transactionService.getAllByUserProfileId(userProfileId);
    }

}
