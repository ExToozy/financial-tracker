package ru.extoozy.repository.transaction.impl;

import ru.extoozy.entity.TransactionEntity;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.repository.transaction.TransactionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class MemoryTransactionRepository implements TransactionRepository {

    private final HashMap<Long, TransactionEntity> transactions = new HashMap<>();

    private long currentId = 1L;

    @Override
    public void save(TransactionEntity transaction) {
        transaction.setId(currentId);
        transactions.put(currentId, transaction);
        currentId += 1;
    }

    @Override
    public void update(TransactionEntity transaction) {
        TransactionEntity transactionToUpdate = transactions.get(transaction.getId());
        if (transactionToUpdate == null) {
            return;
        }
        if (transaction.getAmount() != null) {
            transactionToUpdate.setAmount(transaction.getAmount());
        }
        if (transaction.getCategory() != null) {
            transactionToUpdate.setCategory(transaction.getCategory());
        }
        if (transaction.getDescription() != null) {
            transactionToUpdate.setDescription(transaction.getDescription());
        }
    }

    @Override
    public boolean delete(Long id) {
        TransactionEntity transaction = transactions.remove(id);
        return transaction != null;
    }

    @Override
    public List<TransactionEntity> findAll() {
        return transactions.values().stream().toList();
    }

    @Override
    public TransactionEntity findById(Long id) {
        return Optional.ofNullable(transactions.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Transaction with id=%s not found".formatted(id)));
    }

    @Override
    public List<TransactionEntity> findAllByUserProfileId(Long id) {
        return transactions.values()
                .stream()
                .filter(transaction -> transaction.getUserProfile().getId().equals(id))
                .toList();
    }
}
