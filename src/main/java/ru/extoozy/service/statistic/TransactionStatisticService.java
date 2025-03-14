package ru.extoozy.service.statistic;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Интерфейс для сервиса статистики транзакций пользователя.
 * Содержит методы для получения статистики по транзакциям и балансу пользователя.
 */
public interface TransactionStatisticService {

    /**
     * Получает полную статистику пользователя по транзакциям.
     *
     * @param userProfileId идентификатор профиля пользователя
     * @return карта, содержащая полную статистику пользователя
     */
    Map<String, Object> getFullUserStatistic(Long userProfileId);

    /**
     * Получает статистику транзакций пользователя за указанный период.
     *
     * @param userProfileId идентификатор профиля пользователя
     * @param start         дата начала периода
     * @param end           дата окончания периода
     * @return карта, содержащая статистику транзакций пользователя за указанный период
     */
    Map<String, Object> getUserTransactionsStatisticByPeriod(Long userProfileId, LocalDate start, LocalDate end);

    /**
     * Получает общую сумму выводов по категориям транзакций пользователя.
     *
     * @param userProfileId идентификатор профиля пользователя
     * @return карта, содержащая суммы выводов, сгруппированные по категориям
     */
    Map<String, BigDecimal> getTotalWithdrawalTransactionsAmountGroupedByCategory(Long userProfileId);

    /**
     * Получает баланс пользователя.
     *
     * @param userProfileId идентификатор профиля пользователя
     * @return текущий баланс пользователя
     */
    BigDecimal getUserBalance(Long userProfileId);
}
