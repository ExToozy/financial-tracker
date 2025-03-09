package ru.extoozy.service.statistic;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface TransactionStatisticService {
    Map<String, Object> getFullUserStatistic(Long userProfileId);

    Map<String, Object> getUserTransactionsStatisticByPeriod(Long userProfileId, LocalDate start, LocalDate end);

    Map<String, BigDecimal> getTotalWithdrawalTransactionsAmountGroupedByCategory(Long userProfileId);

    BigDecimal getUserBalance(Long userProfileId);

}
