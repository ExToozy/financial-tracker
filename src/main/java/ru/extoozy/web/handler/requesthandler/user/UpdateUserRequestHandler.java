package ru.extoozy.web.handler.requesthandler.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.context.ApplicationContext;
import ru.extoozy.dto.user.UpdateUserDto;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.mapper.UserMapper;
import ru.extoozy.service.user.UserService;
import ru.extoozy.util.JsonResponse;
import ru.extoozy.util.ServletRequestHelper;
import ru.extoozy.validators.ValidationResult;
import ru.extoozy.validators.jsonvalidator.JsonUserValidator;
import ru.extoozy.web.handler.requesthandler.HttpServletRequestHandler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateUserRequestHandler implements HttpServletRequestHandler {
    public static final UpdateUserRequestHandler INSTANCE = new UpdateUserRequestHandler();

    private final UserService userService = ApplicationContext.getBean(UserService.class);

    @Override
    public JsonResponse handleRequest(HttpServletRequest req) throws Exception {
        var jsonMap = ServletRequestHelper.getJsonMap(req);
        var jsonResponse = new JsonResponse();
        ValidationResult validationResult = JsonUserValidator.validateUpdateUserJson(jsonMap);
        if (validationResult.isValid()) {
            UpdateUserDto dto = UserMapper.INSTANCE.toUpdateDto(jsonMap);
            try {
                userService.update(dto);
            } catch (ResourceNotFoundException e) {
                jsonResponse.addError(e.getMessage());
                jsonResponse.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            jsonResponse.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addAllErrors(validationResult.errors());
        }
        return jsonResponse;
    }
}
