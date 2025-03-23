package ru.extoozy.web.handler.requesthandler.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.context.ApplicationContext;
import ru.extoozy.dto.user.GetUserDto;
import ru.extoozy.dto.user.UserDto;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.mapper.UserMapper;
import ru.extoozy.service.user.UserService;
import ru.extoozy.util.JsonResponse;
import ru.extoozy.util.ServletRequestHelper;
import ru.extoozy.validators.ValidationResult;
import ru.extoozy.validators.jsonvalidator.JsonUserValidator;
import ru.extoozy.web.handler.requesthandler.HttpServletRequestHandler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetUserRequestHandler implements HttpServletRequestHandler {
    public static final GetUserRequestHandler INSTANCE = new GetUserRequestHandler();

    private final UserService userService = ApplicationContext.getBean(UserService.class);

    @Override
    public JsonResponse handleRequest(HttpServletRequest req) throws Exception {
        var jsonMap = ServletRequestHelper.getJsonMap(req);
        var jsonResponse = new JsonResponse();
        ValidationResult validationResult = JsonUserValidator.validateGetUserJson(jsonMap);
        if (validationResult.isValid()) {
            GetUserDto dto = UserMapper.INSTANCE.toGetUserDto(jsonMap);
            try {
                UserDto user = userService.get(dto.getUserId());
                jsonResponse.putDataEntry("user", user);
            } catch (ResourceNotFoundException e) {
                jsonResponse.addError(e.getMessage());
                jsonResponse.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            jsonResponse.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addAllErrors(validationResult.errors());
        }
        return jsonResponse;
    }
}
