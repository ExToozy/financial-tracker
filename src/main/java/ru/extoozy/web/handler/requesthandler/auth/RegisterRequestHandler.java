package ru.extoozy.web.handler.requesthandler.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.annotations.Auditable;
import ru.extoozy.constant.ErrorMessageConstants;
import ru.extoozy.context.ApplicationContext;
import ru.extoozy.dto.user.AuthUserDto;
import ru.extoozy.exception.InvalidEmailException;
import ru.extoozy.exception.UserAlreadyExistsException;
import ru.extoozy.mapper.UserMapper;
import ru.extoozy.service.auth.AuthService;
import ru.extoozy.util.JsonResponse;
import ru.extoozy.util.ServletRequestHelper;
import ru.extoozy.validators.ValidationResult;
import ru.extoozy.validators.jsonvalidator.JsonUserValidator;
import ru.extoozy.web.handler.requesthandler.HttpServletRequestHandler;

@Auditable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegisterRequestHandler implements HttpServletRequestHandler {
    public static final RegisterRequestHandler INSTANCE = new RegisterRequestHandler();

    private final AuthService authService = ApplicationContext.getBean(AuthService.class);

    @Override
    public JsonResponse handleRequest(HttpServletRequest req) throws Exception {
        var jsonMap = ServletRequestHelper.getJsonMap(req);
        var jsonResponse = new JsonResponse();
        ValidationResult validationResult = JsonUserValidator.validateAuthUserJson(jsonMap);
        if (validationResult.isValid()) {
            AuthUserDto dto = UserMapper.INSTANCE.toAuthDto(jsonMap);
            try {
                authService.register(dto);
            } catch (UserAlreadyExistsException e) {
                jsonResponse.addError(ErrorMessageConstants.USER_ALREADY_EXIST_FORMAT.formatted(dto.getEmail()));
                jsonResponse.setStatusCode(HttpServletResponse.SC_CONFLICT);
            } catch (InvalidEmailException e) {
                jsonResponse.addError(ErrorMessageConstants.INVALID_EMAIL_FORMAT.formatted(dto.getEmail()));
                jsonResponse.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            jsonResponse.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addAllErrors(validationResult.errors());
        }
        return jsonResponse;
    }
}
