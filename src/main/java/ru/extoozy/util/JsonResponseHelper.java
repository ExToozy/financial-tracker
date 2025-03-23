package ru.extoozy.util;

import jakarta.servlet.http.HttpServletResponse;
import ru.extoozy.constant.ErrorMessageConstants;

/**
 * Утилита для создания JSON-ответов с различными кодами состояния и сообщениями об ошибках.
 * Предоставляет статические методы для формирования типовых JSON-ответов в зависимости от контекста.
 */
public class JsonResponseHelper {

    /**
     * Создает JSON-ответ для неизвестной конечной точки.
     *
     * @return объект JsonResponse с сообщением об ошибке и кодом состояния 404
     */
    public static JsonResponse getUnknownEndpointResponse() {
        var jsonResponse = new JsonResponse();
        jsonResponse.addError(ErrorMessageConstants.UNKNOWN_ENDPOINT);
        jsonResponse.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
        return jsonResponse;
    }

    /**
     * Создает JSON-ответ для недопустимого метода.
     *
     * @return объект JsonResponse с сообщением об ошибке и кодом состояния 405
     */
    public static JsonResponse getNotAllowedMethodResponse() {
        var jsonResponse = new JsonResponse();
        jsonResponse.addError(ErrorMessageConstants.METHOD_NOT_ALLOWED);
        jsonResponse.setStatusCode(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return jsonResponse;
    }

    /**
     * Создает JSON-ответ для внутренней ошибки сервера.
     *
     * @param e вызванное исключение
     * @return объект JsonResponse с сообщением об ошибке и кодом состояния 500
     */
    public static JsonResponse getInternalServerResponse(Exception e) {
        e.printStackTrace();
        var jsonResponse = new JsonResponse();
        jsonResponse.addError(ErrorMessageConstants.INTERVAL_SERVER_ERROR);
        jsonResponse.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return jsonResponse;
    }

    /**
     * Создает JSON-ответ для несанкционированного доступа или недействительного токена.
     *
     * @return объект JsonResponse с сообщением об ошибке и кодом состояния 401
     */
    public static JsonResponse getUserUnauthorizedOrTokenIsInvalidResponse() {
        var jsonResponse = new JsonResponse();
        jsonResponse.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
        jsonResponse.addError(ErrorMessageConstants.USER_UNAUTHORIZED_OR_TOKEN_INVALID);
        return jsonResponse;
    }
}
