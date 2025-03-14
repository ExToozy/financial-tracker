package ru.extoozy.service.statistic.impl;

import lombok.RequiredArgsConstructor;
import ru.extoozy.entity.TransactionEntity;
import ru.extoozy.enums.TransactionType;
import ru.extoozy.repository.transaction.TransactionRepository;
import ru.extoozy.service.statistic.TransactionStatisticService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class TransactionStatisticServiceImpl implements TransactionStatisticService {

    private final TransactionRepository transactionRepository;

    @Override
    public Map<String, Object> getFullUserStatistic(Long userProfileId) {
        List<TransactionEntity> transactions = transactionRepository.findAllByUserProfileId(userProfileId);
        Map<String, Object> userTransactionsStatistic = getUserTransactionsStatistic(transactions);
        userTransactionsStatistic.put(
                "grouped_by_category",
                getTotalWithdrawalTransactionsAmountGroupedByCategory(userProfileId)
        );
        return userTransactionsStatistic;
    }

    @Override
    public Map<String, Object> getUserTransactionsStatisticByPeriod(Long userProfileId, LocalDate start, LocalDate end) {
        List<TransactionEntity> transactions = transactionRepository.findAllByUserProfileId(userProfileId);
        List<TransactionEntity> transactionsInPeriod = transactions.stream()
                .filter(transaction -> transaction.getCreatedAt().isAfter(start.atStartOfDay()))
                .filter(transaction -> transaction.getCreatedAt().isBefore(end.atStartOfDay()))
                .toList();

        return getUserTransactionsStatistic(transactionsInPeriod);
    }

    @Override
    public Map<String, BigDecimal> getTotalWithdrawalTransactionsAmountGroupedByCategory(Long userProfileId) {
        List<TransactionEntity> transactions = transactionRepository.findAllByUserProfileId(userProfileId);
        HashMap<String, BigDecimal> map = new HashMap<>();
        for (TransactionEntity transaction : transactions) {
            if (transaction.getTransactionType() == TransactionType.WITHDRAWAL) {
                map.putIfAbsent(transaction.getCategory(), BigDecimal.ZERO);
                map.replace(transaction.getCategory(), map.get(transaction.getCategory()).add(transaction.getAmount()));
            }
        }
        return map;
    }

    @Override
    public BigDecimal getUserBalance(Long userProfileId) {
        List<TransactionEntity> transactions = transactionRepository.findAllByUserProfileId(userProfileId);
        BigDecimal balance = BigDecimal.ZERO;
        for (TransactionEntity transaction : transactions) {
            balance = balance.add(transaction.getAmount().multiply(transaction.getTransactionType().getSign()));
        }
        return balance;
    }

    private Map<String, Object> getUserTransactionsStatistic(List<TransactionEntity> transactions) {
        HashMap<String, Object> statistic = new HashMap<>();
        BigDecimal totalSum = BigDecimal.ZERO;
        BigDecimal withdrawalSum = BigDecimal.ZERO;
        BigDecimal replenishSum = BigDecimal.ZERO;

        for (TransactionEntity transaction : transactions) {
            totalSum = totalSum.add(transaction.getAmount().multiply(transaction.getTransactionType().getSign()));
            if (transaction.getTransactionType() == TransactionType.WITHDRAWAL) {
                withdrawalSum = withdrawalSum.add(transaction.getAmount());
            } else {
                replenishSum = replenishSum.add(transaction.getAmount());
            }
        }

        statistic.put("transaction_count", transactions.size());
        statistic.put("withdrawal_sum", withdrawalSum);
        statistic.put("replenish_sum", replenishSum);
        statistic.put("total_sum", totalSum);

        return statistic;
    }
}
