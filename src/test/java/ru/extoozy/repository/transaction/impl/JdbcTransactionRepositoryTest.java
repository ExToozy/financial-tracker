package ru.extoozy.repository.transaction.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.extoozy.config.DbConfig;
import ru.extoozy.entity.TransactionEntity;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.enums.TransactionType;
import ru.extoozy.enums.UserRole;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.migration.MigrationTool;
import ru.extoozy.repository.profile.impl.JdbcUserProfileRepository;
import ru.extoozy.repository.transaction.TransactionRepository;
import ru.extoozy.repository.user.impl.JdbcUserRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JdbcTransactionRepositoryTest {

    private static Connection connection;
    private TransactionRepository repository;

    @BeforeAll
    static void runMigration() throws SQLException {
        DbConfig dbConfig = new DbConfig();
        connection = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getUsername());
        MigrationTool.runMigrate();
        var user = UserEntity.builder()
                .role(UserRole.USER)
                .password("test")
                .email("test")
                .build();
        UserProfileEntity userProfile = UserProfileEntity.builder()
                .firstName("first")
                .lastName("last")
                .user(user)
                .build();

        new JdbcUserRepository().save(user);
        new JdbcUserProfileRepository().save(userProfile);
    }

    @AfterAll
    static void tearDown() throws SQLException {
        connection.close();
    }

    @BeforeEach
    void setUp() {
        repository = new JdbcTransactionRepository();
    }

    @AfterEach
    void deleteTransactions() throws SQLException {
        connection.createStatement().execute("DELETE FROM financial_tracker_schema.transactions");
    }

    @Test
    @DisplayName("Обновление транзакции - существующая транзакция обновляется")
    void testUpdate_whenTransactionExists_thenUpdated() {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionType(TransactionType.REPLENISHMENT);
        transaction.setCategory("Test category");
        transaction.setDescription("Test description");
        transaction.setAmount(BigDecimal.ZERO);
        transaction.setUserProfile(UserProfileEntity.builder().id(1L).build());
        repository.save(transaction);

        transaction.setAmount(BigDecimal.TEN);
        repository.update(transaction);

        assertThat(repository.findById(transaction.getId()).getAmount()).usingComparator(BigDecimal::compareTo).isEqualTo(BigDecimal.TEN);
    }

    @Test
    @DisplayName("Удаление транзакции - транзакция удаляется и возвращается true")
    void testDelete_whenTransactionExists_thenRemoved() {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionType(TransactionType.REPLENISHMENT);
        transaction.setCategory("Test category");
        transaction.setDescription("Test description");
        transaction.setAmount(BigDecimal.ZERO);
        transaction.setUserProfile(UserProfileEntity.builder().id(1L).build());
        repository.save(transaction);

        boolean result = repository.delete(transaction.getId());

        assertThat(result).isTrue();
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Поиск всех транзакций - должен возвращать список")
    void testFindAll_whenTransactionsExist_thenReturnList() {
        TransactionEntity transaction1 = new TransactionEntity();
        transaction1.setTransactionType(TransactionType.REPLENISHMENT);
        transaction1.setCategory("Test category1");
        transaction1.setDescription("Test description1");
        transaction1.setAmount(BigDecimal.ZERO);
        transaction1.setUserProfile(UserProfileEntity.builder().id(1L).build());

        TransactionEntity transaction2 = new TransactionEntity();
        transaction2.setTransactionType(TransactionType.REPLENISHMENT);
        transaction2.setCategory("Test category2");
        transaction2.setDescription("Test description2");
        transaction2.setAmount(BigDecimal.ZERO);
        transaction2.setUserProfile(UserProfileEntity.builder().id(1L).build());

        repository.save(transaction1);
        repository.save(transaction2);

        List<TransactionEntity> transactions = repository.findAll();
        assertThat(transactions).hasSize(2);
    }

    @Test
    @DisplayName("Поиск по ID - должен вернуть найденную транзакцию")
    void testFindById_whenTransactionExists_thenReturnTransaction() {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionType(TransactionType.REPLENISHMENT);
        transaction.setCategory("Test category");
        transaction.setDescription("Test description");
        transaction.setAmount(BigDecimal.ZERO);
        transaction.setUserProfile(UserProfileEntity.builder().id(1L).build());
        repository.save(transaction);

        TransactionEntity foundTransaction = repository.findById(transaction.getId());
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
        TransactionEntity transaction1 = new TransactionEntity();
        transaction1.setTransactionType(TransactionType.REPLENISHMENT);
        transaction1.setCategory("Test category1");
        transaction1.setDescription("Test description1");
        transaction1.setAmount(BigDecimal.ZERO);
        transaction1.setUserProfile(UserProfileEntity.builder().id(1L).build());

        TransactionEntity transaction2 = new TransactionEntity();
        transaction2.setTransactionType(TransactionType.REPLENISHMENT);
        transaction2.setCategory("Test category2");
        transaction2.setDescription("Test description2");
        transaction2.setAmount(BigDecimal.ZERO);
        transaction2.setUserProfile(UserProfileEntity.builder().id(1L).build());

        repository.save(transaction1);
        repository.save(transaction2);

        List<TransactionEntity> transactions = repository.findAllByUserProfileId(1L);
        assertThat(transactions).hasSize(2);
    }
}
