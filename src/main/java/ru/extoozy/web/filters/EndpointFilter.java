package ru.extoozy.web.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.extoozy.constant.ApiEndpointConstants;
import ru.extoozy.util.JsonResponse;
import ru.extoozy.util.JsonResponseHelper;

import java.io.IOException;
import java.util.Arrays;

/**
 * Фильтр для проверки запросов к API на допустимость конечных точек и методов.
 */
@WebFilter(ApiEndpointConstants.ALL_ENDPOINTS)
public class EndpointFilter implements Filter {

    /**
     * Фильтрует запросы к API, проверяя допустимость конечных точек и методов.
     * Если конечная точка не существует или метод недопустим, возвращает соответствующий JSON-ответ об ошибке.
     * В противном случае, передает управление следующему фильтру или сервлету.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String[] endpointMethods = getEndpointMethods(httpRequest.getRequestURI());

        JsonResponse errorResponse = null;
        if (endpointMethods == null) {
            errorResponse = JsonResponseHelper.getUnknownEndpointResponse();
        } else if (!isAllowedMethod(endpointMethods, httpRequest.getMethod())) {
            errorResponse = JsonResponseHelper.getNotAllowedMethodResponse();
        }

        if (errorResponse == null) {
            chain.doFilter(httpRequest, httpResponse);
        } else {
            httpResponse.setContentType("application/json");
            httpResponse.setStatus(errorResponse.getStatusCode());
            httpResponse.getWriter().write(errorResponse.toJsonString());
        }
    }

    /**
     * Возвращает массив допустимых методов для указанной конечной точки.
     *
     * @param requestURI URI запроса
     * @return массив допустимых методов или null, если конечная точка не найдена
     */
    private String[] getEndpointMethods(String requestURI) {
        return switch (requestURI) {
            case ApiEndpointConstants.TRANSACTION_ENDPOINT -> ApiEndpointConstants.TRANSACTION_ENDPOINT_METHODS;
            case ApiEndpointConstants.PROFILE_ENDPOINT -> ApiEndpointConstants.PROFILE_ENDPOINT_METHODS;
            case ApiEndpointConstants.BUDGET_ENDPOINT -> ApiEndpointConstants.BUDGET_ENDPOINT_METHODS;
            case ApiEndpointConstants.GOAL_ENDPOINT -> ApiEndpointConstants.GOAL_ENDPOINT_METHODS;
            case ApiEndpointConstants.USER_ENDPOINT -> ApiEndpointConstants.USER_ENDPOINT_METHODS;
            case ApiEndpointConstants.AUTHORIZATION_ENDPOINT -> ApiEndpointConstants.AUTHORIZATION_ENDPOINT_METHODS;
            case ApiEndpointConstants.REGISTRATION_ENDPOINT -> ApiEndpointConstants.REGISTRATION_ENDPOINT_METHODS;
            default -> null;
        };
    }

    /**
     * Проверяет, допустим ли метод для конечной точки.
     *
     * @param allowedMethods массив допустимых методов
     * @param method         метод запроса
     * @return true, если метод допустим, иначе false
     */
    private boolean isAllowedMethod(String[] allowedMethods, String method) {
        return Arrays.asList(allowedMethods).contains(method);
    }
}
