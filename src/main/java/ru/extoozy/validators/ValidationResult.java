package ru.extoozy.validators;

import java.util.List;

/**
 * Представляет результат валидации, содержащий список ошибок.
 * Предоставляет методы для проверки, является ли результат валидным.
 *
 * @param errors список ошибок, возникающих в процессе валидации
 */
public record ValidationResult(List<String> errors) {

    /**
     * Проверяет, является ли результат валидации валидным.
     *
     * @return true, если список ошибок пуст (валидация успешна), иначе false
     */
    public boolean isValid() {
        return errors.isEmpty();
    }


}
