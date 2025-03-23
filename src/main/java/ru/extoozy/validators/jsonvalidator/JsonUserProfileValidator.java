package ru.extoozy.validators.jsonvalidator;


import ru.extoozy.util.JsonValidator;
import ru.extoozy.validators.ValidationResult;

import java.util.Map;

/**
 * Класс для валидации JSON-объектов, связанных с профилем пользователя.
 * Содержит методы для проверки структуры JSON-данных
 */
public class JsonUserProfileValidator {

    /**
     * Валидация JSON для создания профиля пользователя.
     *
     * @param jsonMap JSON-данные, содержащие информацию для создания профиля пользователя
     * @return результат валидации, содержащий ошибки, если они есть
     */
    public static ValidationResult validateCreateUserProfileJson(Map<String, Object> jsonMap) {
        Map<String, Map<String, Object>> fieldValidationMap = Map.of(
                "firstname", Map.of(
                        "class", String.class
                ),
                "lastname", Map.of(
                        "class", String.class
                )
        );
        return JsonValidator.validateJsonFields(jsonMap, fieldValidationMap);
    }

    /**
     * Валидация JSON для обновления профиля пользователя.
     *
     * @param jsonMap JSON-данные, содержащие информацию для обновления профиля пользователя
     * @return результат валидации, содержащий ошибки, если они есть
     */
    public static ValidationResult validateUpdateUserProfileJson(Map<String, Object> jsonMap) {
        Map<String, Map<String, Object>> fieldValidationMap = Map.of(
                "user_profile_id", Map.of(
                        "class", Long.class
                ),
                "firstname", Map.of(
                        "class", String.class
                ),
                "lastname", Map.of(
                        "class", String.class
                )
        );
        return JsonValidator.validateJsonFields(jsonMap, fieldValidationMap);
    }

    /**
     * Валидация JSON для удаления профиля пользователя.
     *
     * @param jsonMap JSON-данные, содержащие информацию для удаления профиля пользователя
     * @return результат валидации, содержащий ошибки, если они есть
     */
    public static ValidationResult validateDeleteUserProfileJson(Map<String, Object> jsonMap) {
        Map<String, Map<String, Object>> fieldValidationMap = Map.of(
                "user_profile_id", Map.of(
                        "class", Long.class
                )
        );
        return JsonValidator.validateJsonFields(jsonMap, fieldValidationMap);
    }

    /**
     * Валидация JSON для получения профиля пользователя.
     *
     * @param jsonMap JSON-данные, содержащие информацию для получения профиля пользователя
     * @return результат валидации, содержащий ошибки, если они есть
     */
    public static ValidationResult validateGetUserProfileJson(Map<String, Object> jsonMap) {
        Map<String, Map<String, Object>> fieldValidationMap = Map.of(
                "user_id", Map.of(
                        "class", Long.class
                )
        );
        return JsonValidator.validateJsonFields(jsonMap, fieldValidationMap);
    }
}
