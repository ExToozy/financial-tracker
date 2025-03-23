package ru.extoozy.validators.jsonvalidator;

import ru.extoozy.util.JsonValidator;
import ru.extoozy.validators.ValidationResult;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Класс для валидации JSON-объектов, связанных с целями пользователей.
 * Содержит методы для проверки структуры JSON-данных
 */
public class JsonGoalValidator {

    /**
     * Валидация JSON для создания цели.
     *
     * @param jsonMap JSON-данные, содержащие информацию для создания цели
     * @return {@link ValidationResult} результат валидации, содержащий ошибки, если они есть
     */
    public static ValidationResult validateCreateGoalJson(Map<String, Object> jsonMap) {
        Map<String, Map<String, Object>> fieldValidationMap = Map.of(
                "name", Map.of(
                        "class", String.class
                ),
                "goal_amount", Map.of(
                        "class", BigDecimal.class
                )
        );
        return JsonValidator.validateJsonFields(jsonMap, fieldValidationMap);
    }

    /**
     * Валидация JSON для обновления цели.
     *
     * @param jsonMap JSON-данные, содержащие информацию для обновления цели
     * @return {@link ValidationResult} результат валидации, содержащий ошибки, если они есть
     */
    public static ValidationResult validateReplenishGoalJson(Map<String, Object> jsonMap) {
        Map<String, Map<String, Object>> fieldValidationMap = Map.of(
                "goal_id", Map.of(
                        "class", Long.class
                ),
                "amount", Map.of(
                        "class", BigDecimal.class
                )
        );
        return JsonValidator.validateJsonFields(jsonMap, fieldValidationMap);
    }

    /**
     * Валидация JSON для удаления цели.
     *
     * @param jsonMap JSON-данные, содержащие информацию для удаления цели
     * @return {@link ValidationResult} результат валидации, содержащий ошибки, если они есть
     */
    public static ValidationResult validateDeleteGoalJson(Map<String, Object> jsonMap) {
        Map<String, Map<String, Object>> fieldValidationMap = Map.of(
                "goal_id", Map.of(
                        "class", Long.class
                )
        );
        return JsonValidator.validateJsonFields(jsonMap, fieldValidationMap);
    }

    /**
     * Валидация JSON для получения целей.
     *
     * @param jsonMap JSON-данные, содержащие информацию для получения целей
     * @return {@link ValidationResult} результат валидации, содержащий ошибки, если они есть
     */
    public static ValidationResult validateGetGoalsJson(Map<String, Object> jsonMap) {
        Map<String, Map<String, Object>> fieldValidationMap = Map.of(
                "user_profile_id", Map.of(
                        "class", Long.class
                )
        );
        return JsonValidator.validateJsonFields(jsonMap, fieldValidationMap);
    }
}
