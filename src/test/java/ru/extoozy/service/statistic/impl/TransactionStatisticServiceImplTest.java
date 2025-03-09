package ru.extoozy.service.statistic.impl;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.extoozy.entity.TransactionEntity;
import ru.extoozy.enums.TransactionType;
import ru.extoozy.repository.transaction.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class TransactionStatisticServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionStatisticServiceImpl transactionStatisticService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Получение полной статистики пользователя")
    void getFullUserStatistic_whenUserProfileIdExists_thenReturnUserStatistic() {
        Long userProfileId = 1L;
        TransactionEntity transaction1 = TransactionEntity.builder()
                .transactionType(TransactionType.WITHDRAWAL)
                .category("Продукты")
                .amount(BigDecimal.valueOf(100))
                .createdAt(LocalDateTime.now())
                .build();
        TransactionEntity transaction2 = TransactionEntity.builder()
                .transactionType(TransactionType.WITHDRAWAL)
                .category("Продукты")
                .amount(BigDecimal.valueOf(200))
                .createdAt(LocalDateTime.now())
                .build();
        TransactionEntity transaction3 = TransactionEntity.builder()
                .transactionType(TransactionType.WITHDRAWAL)
                .category("Одежда")
                .amount(BigDecimal.valueOf(1000))
                .createdAt(LocalDateTime.now())
                .build();
        TransactionEntity transaction4 = TransactionEntity.builder()
                .transactionType(TransactionType.REPLENISHMENT)
                .amount(BigDecimal.valueOf(100))
                .createdAt(LocalDateTime.now())
                .build();

        when(transactionRepository.findAllByUserProfileId(userProfileId)).thenReturn(
                Arrays.asList(
                        transaction1,
                        transaction2,
                        transaction3,
                        transaction4
                )
        );

        Map<String, Object> result = transactionStatisticService.getFullUserStatistic(userProfileId);
        System.out.println(result);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(result).containsEntry("withdrawal_sum", BigDecimal.valueOf(1300));
        softly.assertThat(result).containsEntry("total_sum", BigDecimal.valueOf(-1200));
        softly.assertThat(result).containsEntry("replenish_sum", BigDecimal.valueOf(100));
        softly.assertThat(result).containsEntry("transaction_count", 4);
        softly.assertThat(result).containsEntry("grouped_by_category", Map.of("Продукты", BigDecimal.valueOf(300), "Одежда", BigDecimal.valueOf(1000)));

        softly.assertAll();
    }

    @Test
    @DisplayName("Получение статистики транзакций пользователя по периоду")
    void getUserTransactionsStatisticByPeriod_whenTransactionsExistInPeriod_thenReturnStatistics() {
        Long userProfileId = 1L;
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now();
        TransactionEntity transaction1 = TransactionEntity.builder()
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(BigDecimal.valueOf(100))
                .createdAt(LocalDateTime.now())
                .build();
        TransactionEntity transaction2 = TransactionEntity.builder()
                .transactionType(TransactionType.REPLENISHMENT)
                .amount(BigDecimal.valueOf(100))
                .createdAt(LocalDateTime.now())
                .build();

        when(transactionRepository.findAllByUserProfileId(userProfileId)).thenReturn(Arrays.asList(transaction1, transaction2));

        Map<String, Object> result = transactionStatisticService.getUserTransactionsStatisticByPeriod(userProfileId, start, end);

        assertThat(result).containsKey("transaction_count");
    }

    @Test
    @DisplayName("Получение суммы снятий по категориям")
    void getTotalWithdrawalTransactionsAmountGroupedByCategory_whenWithdrawalsExist_thenReturnGroupedSums() {
        Long userProfileId = 1L;
        TransactionEntity transaction1 = TransactionEntity.builder()
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(BigDecimal.valueOf(100))
                .createdAt(LocalDateTime.now())
                .build();
        TransactionEntity transaction2 = TransactionEntity.builder()
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(BigDecimal.valueOf(200))
                .createdAt(LocalDateTime.now())
                .build();
        transaction1.setCategory("Food");
        transaction2.setCategory("Food");

        when(transactionRepository.findAllByUserProfileId(userProfileId)).thenReturn(Arrays.asList(transaction1, transaction2));

        Map<String, BigDecimal> result = transactionStatisticService.getTotalWithdrawalTransactionsAmountGroupedByCategory(userProfileId);

        assertThat(result).containsEntry("Food", BigDecimal.valueOf(300));
    }

    @Test
    @DisplayName("Получение баланса пользователя")
    void getUserBalance_whenTransactionsExist_thenReturnBalance() {
        Long userProfileId = 1L;
        TransactionEntity transaction1 = TransactionEntity.builder()
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(BigDecimal.valueOf(100))
                .createdAt(LocalDateTime.now())
                .build();
        TransactionEntity transaction2 = TransactionEntity.builder()
                .transactionType(TransactionType.REPLENISHMENT)
                .amount(BigDecimal.valueOf(200))
                .createdAt(LocalDateTime.now())
                .build();

        when(transactionRepository.findAllByUserProfileId(userProfileId)).thenReturn(Arrays.asList(transaction1, transaction2));

        BigDecimal result = transactionStatisticService.getUserBalance(userProfileId);

        assertThat(result).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("Получение статистики пользователя, когда нет транзакций")
    void getUserTransactionsStatistic_whenNoTransactions_thenReturnEmptyStatistic() {
        Long userProfileId = 1L;

        when(transactionRepository.findAllByUserProfileId(userProfileId)).thenReturn(List.of());

        Map<String, Object> result = transactionStatisticService.getUserTransactionsStatisticByPeriod(userProfileId, LocalDate.now().minusDays(1), LocalDate.now());

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(result).containsEntry("transaction_count", 0);
        softly.assertThat(result).containsEntry("withdrawal_sum", BigDecimal.ZERO);
        softly.assertThat(result).containsEntry("replenish_sum", BigDecimal.ZERO);
        softly.assertThat(result).containsEntry("total_sum", BigDecimal.ZERO);

        softly.assertAll();
    }

    @Test
    @DisplayName("Получение суммы снятий, когда нет транзакций")
    void getTotalWithdrawalTransactionsAmountGroupedByCategory_whenNoWithdrawals_thenReturnEmptyMap() {
        Long userProfileId = 1L;

        when(transactionRepository.findAllByUserProfileId(userProfileId)).thenReturn(List.of());

        Map<String, BigDecimal> result = transactionStatisticService.getTotalWithdrawalTransactionsAmountGroupedByCategory(userProfileId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Получение баланса пользователя, когда нет транзакций")
    void getUserBalance_whenNoTransactions_thenReturnZeroBalance() {
        Long userProfileId = 1L;

        when(transactionRepository.findAllByUserProfileId(userProfileId)).thenReturn(List.of());

        BigDecimal result = transactionStatisticService.getUserBalance(userProfileId);

        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }
}