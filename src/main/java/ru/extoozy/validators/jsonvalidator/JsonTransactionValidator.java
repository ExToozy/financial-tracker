package ru.extoozy.validators.jsonvalidator;

import ru.extoozy.enums.TransactionType;
import ru.extoozy.util.JsonValidator;
import ru.extoozy.validators.ValidationResult;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

/**
 * Класс для валидации JSON-объектов, связанных с транзакциями.
 * Содержит методы для проверки структуры JSON-данных
 */
public class JsonTransactionValidator {

    /**
     * Валидация JSON для создания транзакции.
     *
     * @param jsonMap JSON-данные, содержащие информацию для создания транзакции
     * @return {@link ValidationResult} результат валидации, содержащий ошибки, если они есть
     */
    public static ValidationResult validateCreateTransactionJson(Map<String, Object> jsonMap) {
        Map<String, Map<String, Object>> fieldValidationMap = Map.of(
                "category", Map.of(
                        "class", String.class
                ),
                "description", Map.of(
                        "class", String.class
                ),
                "transaction_type", Map.of(
                        "class", String.class,
                        "choices", Arrays.stream(TransactionType.values()).map(Enum::toString).toArray()
                ),
                "amount", Map.of(
                        "class", BigDecimal.class
                )
        );
        return JsonValidator.validateJsonFields(jsonMap, fieldValidationMap);
    }

    /**
     * Валидация JSON для обновления транзакции.
     *
     * @param jsonMap JSON-данные, содержащие информацию для обновления транзакции
     * @return {@link ValidationResult} результат валидации, содержащий ошибки, если они есть
     */
    public static ValidationResult validateUpdateTransactionJson(Map<String, Object> jsonMap) {
        Map<String, Map<String, Object>> fieldValidationMap = Map.of(
                "transaction_id", Map.of(
                        "class", Long.class
                ),
                "category", Map.of(
                        "class", String.class
                ),
                "description", Map.of(
                        "class", String.class
                ),
                "amount", Map.of(
                        "class", BigDecimal.class
                )
        );
        return JsonValidator.validateJsonFields(jsonMap, fieldValidationMap);
    }

    /**
     * Валидация JSON для удаления транзакции.
     *
     * @param jsonMap JSON-данные, содержащие информацию для удаления транзакции
     * @return {@link ValidationResult} результат валидации, содержащий ошибки, если они есть
     */
    public static ValidationResult validateDeleteTransactionJson(Map<String, Object> jsonMap) {
        Map<String, Map<String, Object>> fieldValidationMap = Map.of(
                "transaction_id", Map.of(
                        "class", Long.class
                )
        );
        return JsonValidator.validateJsonFields(jsonMap, fieldValidationMap);
    }

    /**
     * Валидация JSON для получения транзакций.
     *
     * @param jsonMap JSON-данные, содержащие информацию для получения транзакций
     * @return {@link ValidationResult} результат валидации, содержащий ошибки, если они есть
     */
    public static ValidationResult validateGetTransactionsJson(Map<String, Object> jsonMap) {
        Map<String, Map<String, Object>> fieldValidationMap = Map.of(
                "user_profile_id", Map.of(
                        "class", Long.class
                )
        );
        return JsonValidator.validateJsonFields(jsonMap, fieldValidationMap);
    }
}
