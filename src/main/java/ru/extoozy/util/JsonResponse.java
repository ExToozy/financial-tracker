package ru.extoozy.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс для представления JSON-ответов, который включает в себя
 * данные, статус код и список ошибок.
 * Обеспечивает методы для настройки и преобразования ответа в JSON-строку.
 */
public class JsonResponse {

    private static final String DATA_KEY = "data";
    private static final String STATUS_CODE_KEY = "status_code";
    private static final String ERRORS_KEY = "errors";

    private final Map<String, Object> responseMap;

    /**
     * Конструктор, который инициализирует {@code responseMap}
     * значениями по умолчанию.
     */
    public JsonResponse() {
        responseMap = getDefaultResponseMap();
    }

    /**
     * Получает карту ответа со значениями по умолчанию.
     *
     * @return map с полями "errors", "data" и "status_code"
     */
    private Map<String, Object> getDefaultResponseMap() {
        Map<String, Object> mapResponse = new HashMap<>();
        mapResponse.put(ERRORS_KEY, new ArrayList<String>());
        mapResponse.put(DATA_KEY, new HashMap<String, Object>());
        mapResponse.put(STATUS_CODE_KEY, HttpServletResponse.SC_OK);
        return mapResponse;
    }

    /**
     * Получает статус код ответа.
     *
     * @return статус код ответа
     */
    public int getStatusCode() {
        return (int) responseMap.get(STATUS_CODE_KEY);
    }

    /**
     * Устанавливает статус код ответа.
     *
     * @param statusCode новый статус код ответа
     */
    public void setStatusCode(int statusCode) {
        responseMap.replace(STATUS_CODE_KEY, statusCode);
    }

    /**
     * Добавляет запись данных в ответ.
     *
     * @param key   ключ для данных
     * @param value значение данных
     */
    public void putDataEntry(String key, Object value) {
        ((Map<String, Object>) responseMap.get(DATA_KEY)).put(key, value);
    }

    /**
     * Добавляет одну ошибку в список ошибок ответа.
     *
     * @param error сообщение об ошибке
     */
    public void addError(String error) {
        ((List<String>) responseMap.get(ERRORS_KEY)).add(error);
    }

    /**
     * Добавляет несколько ошибок в список ошибок ответа.
     *
     * @param errors список сообщений об ошибках
     */
    public void addAllErrors(List<String> errors) {
        ((List<String>) responseMap.get(ERRORS_KEY)).addAll(errors);
    }

    /**
     * Преобразует ответ в строку JSON.
     *
     * @return строка JSON, представляющая ответ
     * @throws JsonProcessingException если возникает ошибка при обработке JSON
     */
    public String toJsonString() throws JsonProcessingException {
        return JsonMapper.jsonMapToString(responseMap);
    }
}
