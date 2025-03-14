package ru.extoozy.controller;

import lombok.RequiredArgsConstructor;
import ru.extoozy.service.statistic.TransactionStatisticService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Контроллер для получения статистики по транзакциям пользователей.
 * Содержит методы для получения общей статистики, статистики за период, суммы вывода по категориям и баланса пользователя.
 */
@RequiredArgsConstructor
public class TransactionStatisticController {

    private final TransactionStatisticService transactionStatisticService;

    /**
     * Получает полную статистику по транзакциям пользователя.
     *
     * @param userProfileId идентификатор профиля пользователя
     * @return карта, содержащая полную статистику по транзакциям пользователя
     */
    public Map<String, Object> getFullUserStatistic(Long userProfileId) {
        return transactionStatisticService.getFullUserStatistic(userProfileId);
    }

    /**
     * Получает статистику транзакций пользователя за указанный период.
     *
     * @param userProfileId идентификатор профиля пользователя
     * @param start         дата начала периода
     * @param end           дата окончания периода
     * @return карта, содержащая статистику транзакций пользователя за указанный период
     */
    public Map<String, Object> getUserStatisticByPeriod(Long userProfileId, LocalDate start, LocalDate end) {
        return transactionStatisticService.getUserTransactionsStatisticByPeriod(userProfileId, start, end);
    }

    /**
     * Получает общую сумму вывода транзакций пользователя, сгруппированную по категориям.
     *
     * @param userProfileId идентификатор профиля пользователя
     * @return карта, где ключ — категория, а значение — сумма вывода по этой категории
     */
    public Map<String, BigDecimal> getTotalWithdrawalAmountGropedByCategory(Long userProfileId) {
        return transactionStatisticService.getTotalWithdrawalTransactionsAmountGroupedByCategory(userProfileId);
    }

    /**
     * Получает текущий баланс пользователя.
     *
     * @param userProfileId идентификатор профиля пользователя
     * @return текущий баланс пользователя
     */
    public BigDecimal getUserBalance(Long userProfileId) {
        return transactionStatisticService.getUserBalance(userProfileId);
    }
}
