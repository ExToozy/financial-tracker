package ru.extoozy.util;

import org.apache.commons.lang3.StringUtils;
import ru.extoozy.constant.ErrorMessageConstants;
import ru.extoozy.validators.ValidationResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Утилита для валидации JSON полей.
 * Содержит методы для проверки наличия, типов и значений полей JSON.
 */
public class JsonValidator {
    private JsonValidator() {
    }

    /**
     * Валидирует поля JSON по заданным критериям.
     *
     * @param jsonMap            map, представляющий JSON
     * @param fieldValidationMap map, содержащий правила валидации для каждого поля
     * @return объект {@link ValidationResult} с ошибками валидации
     */
    public static ValidationResult validateJsonFields(
            Map<String, Object> jsonMap,
            Map<String, Map<String, Object>> fieldValidationMap) {
        List<String> errors = new ArrayList<>();

        if (jsonMap == null) {
            errors.add(ErrorMessageConstants.JSON_IS_NULL);
            return new ValidationResult(errors);
        }

        for (var field : fieldValidationMap.entrySet()) {

            String fieldName = field.getKey();
            Object fieldValue = jsonMap.get(fieldName);
            Class<?> fieldClass = (Class<?>) field.getValue().get("class");
            Object[] choices = (Object[]) field.getValue().get("choices");
            Boolean nullable = (Boolean) field.getValue().get("nullable");

            String fieldError = getFieldError(jsonMap, fieldName, fieldClass, fieldValue, choices, nullable);

            if (fieldError != null) {
                errors.add(fieldError);
            }
        }
        return new ValidationResult(errors);
    }

    /**
     * Получает ошибку поля на основе заданных критериев.
     *
     * @param jsonMap    map, представляющий JSON
     * @param fieldName  имя проверяемого поля
     * @param fieldClass ожидаемый класс значения поля
     * @param fieldValue текущее значение поля
     * @param choices    допустимые значения для поля
     * @param nullable   флаг, указывающий, может ли поле быть null
     * @return сообщение об ошибке поля или null, если ошибок нет
     */
    private static String getFieldError(
            Map<String, Object> jsonMap,
            String fieldName,
            Class<?> fieldClass,
            Object fieldValue,
            Object[] choices,
            Boolean nullable
    ) {
        String error = null;
        if (isFieldValueNotContainsInJson(jsonMap, fieldName)) {
            error = String.format(ErrorMessageConstants.JSON_REQUIRED_FIELD_FORMAT, fieldName);
        } else if (IsFieldNullableAndEqualNull(fieldValue, nullable)) {
            return error;

        } else if (IsFieldValueNull(jsonMap, fieldName)) {
            error = String.format(ErrorMessageConstants.JSON_FIELD_MUST_NOT_BE_NULL_FORMAT, fieldName);

        } else if (isIncorrectFieldValueClass(fieldClass, fieldValue)) {
            error = String.format(ErrorMessageConstants.FIELD_TYPE_ERROR, fieldName, fieldClass);

        } else if (isFieldValueNotInChoises(choices, fieldValue)) {
            String choicesStr = StringUtils.joinWith(", ", choices);
            error = String.format(ErrorMessageConstants.FIELD_NOT_IN_CHOICES_FORMAT, fieldName, choicesStr);
        }
        return error;
    }

    /**
     * Проверяет, является ли поле допустимым и равно ли null, если оно может быть null.
     *
     * @param fieldValue значение поля
     * @param nullable   флаг, указывающий, может ли поле быть null
     * @return true, если поле может быть null и значение равно null
     */
    private static boolean IsFieldNullableAndEqualNull(Object fieldValue, Boolean nullable) {
        return nullable != null && nullable && fieldValue == null;
    }

    /**
     * Проверяет, содержится ли поле в JSON.
     *
     * @param jsonMap   map, представляющий JSON
     * @param fieldName имя проверяемого поля
     * @return true, если поле отсутствует в JSON
     */
    private static boolean isFieldValueNotContainsInJson(Map<String, Object> jsonMap, String fieldName) {
        return !jsonMap.containsKey(fieldName);
    }

    /**
     * Проверяет, равно ли значение поля null.
     *
     * @param jsonMap   map, представляющий JSON
     * @param fieldName имя проверяемого поля
     * @return true, если значение поля равно null
     */
    private static boolean IsFieldValueNull(Map<String, Object> jsonMap, String fieldName) {
        return jsonMap.get(fieldName) == null;
    }

    /**
     * Проверяет, соответствует ли класс значения ожидаемому классу.
     *
     * @param fieldClass ожидаемый класс значения поля
     * @param fieldValue текущее значение поля
     * @return true, если класс значения не соответствует ожидаемому
     */
    private static boolean isIncorrectFieldValueClass(Class<?> fieldClass, Object fieldValue) {
        if (fieldClass.equals(Long.class)) {
            return !fieldValue.getClass().equals(Integer.class);
        }
        if (fieldClass.equals(BigDecimal.class)) {
            return !fieldValue.getClass().equals(Double.class)
                    && !fieldValue.getClass().equals(Integer.class);
        }
        return !fieldClass.isInstance(fieldValue);
    }

    /**
     * Проверяет, содержится ли значение поля в допустимых значениях.
     *
     * @param choices    допустимые значения для поля
     * @param fieldValue текущее значение поля
     * @return true, если значение поля не содержится в допустимых значениях
     */
    private static boolean isFieldValueNotInChoises(Object[] choices, Object fieldValue) {
        return choices != null && !Arrays.asList(choices).contains(fieldValue);
    }
}
