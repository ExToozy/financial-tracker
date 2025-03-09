package ru.extoozy.controller;

import lombok.RequiredArgsConstructor;
import ru.extoozy.service.statistic.TransactionStatisticService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RequiredArgsConstructor
public class TransactionStatisticController {

    private final TransactionStatisticService transactionStatisticService;

    public Map<String, Object> getFullUserStatistic(Long userProfileId) {
        return transactionStatisticService.getFullUserStatistic(userProfileId);
    }

    public Map<String, Object> getUserStatisticByPeriod(Long userProfileId, LocalDate start, LocalDate end) {
        return transactionStatisticService.getUserTransactionsStatisticByPeriod(userProfileId, start, end);
    }

    public Map<String, BigDecimal> getTotalWithdrawalAmountGropedByCategory(Long userProfileId) {
        return transactionStatisticService.getTotalWithdrawalTransactionsAmountGroupedByCategory(userProfileId);
    }

    public BigDecimal getUserBalance(Long userProfileId) {
        return transactionStatisticService.getUserBalance(userProfileId);
    }
}
