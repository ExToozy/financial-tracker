package ru.extoozy.repository.transaction.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.extoozy.entity.TransactionEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemoryTransactionRepositoryTest {

    private MemoryTransactionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new MemoryTransactionRepository();
    }

    @Test
    @DisplayName("Сохранение транзакции - должна добавляться в хранилище")
    void testSave_whenTransactionProvided_thenStored() {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setAmount(BigDecimal.TEN);
        repository.save(transaction);

        assertThat(repository.findAll()).hasSize(1);
        assertThat(repository.findAll().get(0).getAmount()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    @DisplayName("Обновление транзакции - существующая транзакция обновляется")
    void testUpdate_whenTransactionExists_thenUpdated() {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setAmount(BigDecimal.ONE);
        repository.save(transaction);

        transaction.setAmount(BigDecimal.TEN);
        repository.update(transaction);

        assertThat(repository.findById(1L).getAmount()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    @DisplayName("Удаление транзакции - транзакция удаляется и возвращается true")
    void testDelete_whenTransactionExists_thenRemoved() {
        TransactionEntity transaction = new TransactionEntity();
        repository.save(transaction);

        boolean result = repository.delete(1L);

        assertThat(result).isTrue();
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Поиск всех транзакций - должен возвращать список")
    void testFindAll_whenTransactionsExist_thenReturnList() {
        repository.save(new TransactionEntity());
        repository.save(new TransactionEntity());

        List<TransactionEntity> transactions = repository.findAll();
        assertThat(transactions).hasSize(2);
    }

    @Test
    @DisplayName("Поиск по ID - должен вернуть найденную транзакцию")
    void testFindById_whenTransactionExists_thenReturnTransaction() {
        TransactionEntity transaction = new TransactionEntity();
        repository.save(transaction);

        TransactionEntity foundTransaction = repository.findById(1L);
        assertThat(foundTransaction).isNotNull();
    }

    @Test
    @DisplayName("Поиск по ID - выбрасывает исключение, если не найдено")
    void testFindById_whenTransactionNotFound_thenThrowException() {
        assertThatThrownBy(() -> repository.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Transaction with id=1 not found");
    }

    @Test
    @DisplayName("Поиск по ID профиля пользователя - должен вернуть список транзакций")
    void testFindAllByUserProfileId_whenTransactionsExist_thenReturnList() {
        UserProfileEntity user = new UserProfileEntity();
        user.setId(100L);

        TransactionEntity transaction1 = new TransactionEntity();
        transaction1.setUserProfile(user);
        repository.save(transaction1);

        TransactionEntity transaction2 = new TransactionEntity();
        transaction2.setUserProfile(user);
        repository.save(transaction2);

        List<TransactionEntity> transactions = repository.findAllByUserProfileId(100L);
        assertThat(transactions).hasSize(2);
    }
}
