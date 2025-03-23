package ru.extoozy.web.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.extoozy.annotations.Loggable;
import ru.extoozy.util.JsonResponse;
import ru.extoozy.util.JsonResponseHelper;
import ru.extoozy.web.handler.requesthandler.HttpServletRequestHandler;

import java.io.IOException;


/**
 * Класс-утилита для обработки HTTP-запросов с возвратом JSON-ответов.
 * Этот класс предоставляет статический метод {@link #handle(HttpServletRequest, HttpServletResponse, HttpServletRequestHandler)}
 * для обработки HTTP-запросов, установления статуса ответа, и отправки JSON-данных.
 */
@Loggable
public class HttpServletHandler {

    /**
     * Приватный конструктор для предотвращения создания экземпляра этого класса.
     */
    private HttpServletHandler() {
    }

    /**
     * Обрабатывает запрос HTTP, используя предоставленный обработчик запроса {@code requestHandler}.
     * Устанавливает тип контента ответа как "application/json" и записывает JSON-ответ, полученный от {@code requestHandler}.
     * В случае возникновения исключения устанавливается код состояния 500, а тело ответа содержит сообщение об ошибке.
     *
     * @param request        объект запроса {@link HttpServletRequest}
     * @param response       объект ответа {@link HttpServletResponse}
     * @param requestHandler функциональный интерфейс {@link HttpServletRequestHandler}, обрабатывающий запрос и возвращающий JSON-ответ
     * @throws IOException если произошла ошибка при записи ответа
     */
    public static void handle(HttpServletRequest request, HttpServletResponse response, HttpServletRequestHandler requestHandler) throws IOException {
        JsonResponse jsonResponse;
        try {
            jsonResponse = requestHandler.handleRequest(request);
        } catch (Exception e) {
            jsonResponse = JsonResponseHelper.getInternalServerResponse(e);
        }
        response.setContentType("application/json");
        response.setStatus(jsonResponse.getStatusCode());
        response.getWriter().write(jsonResponse.toJsonString());
    }
}
