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
import ru.extoozy.context.ApplicationContext;
import ru.extoozy.context.UserContext;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.repository.user.UserRepository;
import ru.extoozy.util.JsonResponseHelper;
import ru.extoozy.util.TokenHelper;

import java.io.IOException;

@WebFilter(ApiEndpointConstants.ALL_ENDPOINTS)
public class AuthFilter implements Filter {

    UserRepository userRepository = ApplicationContext.getBean(UserRepository.class);


    /**
     * Фильтрует запросы к API, проверяя авторизацию пользователя.
     * Если запрос направлен на конечную точку авторизации или токен действителен и пользователь найден,
     * запрос передается дальше. В противном случае возвращается ответ о несанкционированном доступе.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String token = req.getHeader("Authorization");
        if (isAuthEndpoint(req)) {
            chain.doFilter(req, res);
        } else if (tokenIsValidAndUserFound(token)) {
            UserEntity user = userRepository.findById(TokenHelper.getUserIdFromToken(token));
            UserContext.setUser(user);
            chain.doFilter(req, res);
        } else {
            res.setContentType("application/json");
            var jsonResponse = JsonResponseHelper.getUserUnauthorizedOrTokenIsInvalidResponse();
            res.setStatus(jsonResponse.getStatusCode());
            res.getWriter().write(jsonResponse.toJsonString());
        }
    }

    /**
     * Проверяет, действителен ли токен и существует ли пользователь с идентификатором из токена.
     *
     * @param token токен авторизации
     * @return true, если токен действителен и пользователь найден, иначе false
     */
    private boolean tokenIsValidAndUserFound(String token) {
        boolean validToken = TokenHelper.isValidToken(token);
        if (validToken) {
            try {
                userRepository.findById(TokenHelper.getUserIdFromToken(token));
                return true;
            } catch (ResourceNotFoundException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Проверяет, является ли запрашиваемый URI конечной точкой входа или регистрации.
     *
     * @param req HTTP-запрос
     * @return true, если запрашиваемый URI соответствует конечной точке входа или регистрации, иначе false
     */
    private boolean isAuthEndpoint(HttpServletRequest req) {
        return req.getRequestURI().endsWith(ApiEndpointConstants.AUTHORIZATION_ENDPOINT)
                || req.getRequestURI().endsWith(ApiEndpointConstants.REGISTRATION_ENDPOINT);
    }
}
