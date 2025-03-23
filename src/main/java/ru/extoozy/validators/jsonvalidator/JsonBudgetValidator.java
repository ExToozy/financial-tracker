package ru.extoozy.validators.jsonvalidator;

import ru.extoozy.util.JsonValidator;
import ru.extoozy.validators.ValidationResult;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Класс для валидации JSON-объектов, связанных с бюджетом пользователя.
 * <p>
 * Содержит методы для проверки структуры JSON-данных
 */
public class JsonBudgetValidator {

    /**
     * Валидация JSON для создания бюджета.
     *
     * @param jsonMap JSON-данные, содержащие информацию для создания бюджета
     * @return результат валидации, содержащий ошибки, если они есть
     */
    public static ValidationResult validateCreateBudgetJson(Map<String, Object> jsonMap) {
        Map<String, Map<String, Object>> fieldValidationMap = Map.of(
                "max_amount", Map.of(
                        "class", BigDecimal.class
                )
        );
        return JsonValidator.validateJsonFields(jsonMap, fieldValidationMap);
    }

    /**
     * Валидация JSON для обновления бюджета.
     *
     * @param jsonMap JSON-данные, содержащие информацию для обновления бюджета
     * @return результат валидации, содержащий ошибки, если они есть
     */
    public static ValidationResult validateUpdateBudgetJson(Map<String, Object> jsonMap) {
        Map<String, Map<String, Object>> fieldValidationMap = Map.of(
                "budget_id", Map.of(
                        "class", Long.class
                ),
                "max_amount", Map.of(
                        "class", BigDecimal.class
                )
        );
        return JsonValidator.validateJsonFields(jsonMap, fieldValidationMap);
    }

    public static ValidationResult validateGetBudgetJson(Map<String, Object> jsonMap) {
        Map<String, Map<String, Object>> fieldValidationMap = Map.of(
                "user_profile_id", Map.of(
                        "class", Long.class
                )
        );
        return JsonValidator.validateJsonFields(jsonMap, fieldValidationMap);
    }
}
