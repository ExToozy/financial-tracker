package ru.extoozy.web.handler.requesthandler.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.annotations.Auditable;
import ru.extoozy.constant.ErrorMessageConstants;
import ru.extoozy.context.ApplicationContext;
import ru.extoozy.dto.user.AuthUserDto;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.exception.UserIsBlockedException;
import ru.extoozy.mapper.UserMapper;
import ru.extoozy.service.auth.AuthService;
import ru.extoozy.util.JsonResponse;
import ru.extoozy.util.ServletRequestHelper;
import ru.extoozy.validators.ValidationResult;
import ru.extoozy.validators.jsonvalidator.JsonUserValidator;
import ru.extoozy.web.handler.requesthandler.HttpServletRequestHandler;

@Auditable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginRequestHandler implements HttpServletRequestHandler {

    public static final LoginRequestHandler INSTANCE = new LoginRequestHandler();

    private final AuthService authService = ApplicationContext.getBean(AuthService.class);

    @Override
    public JsonResponse handleRequest(HttpServletRequest req) throws Exception {
        var jsonMap = ServletRequestHelper.getJsonMap(req);
        var jsonResponse = new JsonResponse();
        ValidationResult validationResult = JsonUserValidator.validateAuthUserJson(jsonMap);
        if (validationResult.isValid()) {
            AuthUserDto dto = UserMapper.INSTANCE.toAuthDto(jsonMap);
            try {
                String userToken = authService.authenticate(dto);
                jsonResponse.putDataEntry("token", userToken);
            } catch (ResourceNotFoundException e) {
                jsonResponse.addError(e.getMessage());
                jsonResponse.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
            } catch (UserIsBlockedException e) {
                jsonResponse.addError(ErrorMessageConstants.USER_BLOCKED);
                jsonResponse.setStatusCode(HttpServletResponse.SC_FORBIDDEN);
            }
        } else {
            jsonResponse.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addAllErrors(validationResult.errors());
        }
        return jsonResponse;
    }
}
