package ru.extoozy.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

/**
 * Утилита для работы с JSON, предоставляющая методы для
 * преобразования JSON-карт в строки и наоборот с использованием
 * библиотеки Jackson.
 */
public class JsonMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.findAndRegisterModules();
    }

    /**
     * Преобразует map JSON в строку JSON.
     *
     * @param jsonMap map JSON для преобразования
     * @return строка, представляющая JSON
     * @throws JsonProcessingException если возникает ошибка при преобразовании
     */
    public static String jsonMapToString(Map<String, Object> jsonMap) throws JsonProcessingException {
        return MAPPER.writeValueAsString(jsonMap);
    }

    /**
     * Читает JSON из переданного Reader и преобразует его в map.
     *
     * @param reader Reader для чтения JSON
     * @return карта, представляющая JSON
     * @throws IOException если возникает ошибка при чтении или обработке JSON
     */
    public static Map<String, Object> getJsonMap(Reader reader) throws IOException {
        JsonNode jsonNode = MAPPER.readTree(reader);
        return MAPPER.convertValue(jsonNode, new TypeReference<>() {
        });
    }
}
