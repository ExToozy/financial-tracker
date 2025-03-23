package ru.extoozy.util;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Map;

/**
 * Утилита для обработки HTTP-запросов и извлечения данных из них.
 * Содержит методы для работы с запросами и получения данных в формате JSON.
 */
public class ServletRequestHelper {
    /**
     * Извлекает map из JSON, переданного в теле HTTP-запроса.
     *
     * @param req HTTP-запрос, из которого необходимо извлечь данные
     * @return map, содержащая данные JSON
     * @throws IOException если произошла ошибка при чтении из запроса
     */
    public static Map<String, Object> getJsonMap(HttpServletRequest req) throws IOException {
        return JsonMapper.getJsonMap(req.getReader());
    }
}
