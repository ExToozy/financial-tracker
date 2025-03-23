package ru.extoozy.web.handler.requesthandler;

import jakarta.servlet.http.HttpServletRequest;
import ru.extoozy.util.JsonResponse;

/**
 * Иинтерфейс для обработки HTTP-запросов и возврата JSON-ответов.
 */
public interface HttpServletRequestHandler {
    JsonResponse handleRequest(HttpServletRequest req) throws Exception;
}
