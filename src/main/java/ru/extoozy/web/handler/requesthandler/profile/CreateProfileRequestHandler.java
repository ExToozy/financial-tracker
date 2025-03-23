package ru.extoozy.web.handler.requesthandler.profile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.context.ApplicationContext;
import ru.extoozy.dto.profile.CreateUserProfileDto;
import ru.extoozy.mapper.UserProfileMapper;
import ru.extoozy.service.profile.UserProfileService;
import ru.extoozy.util.JsonResponse;
import ru.extoozy.util.ServletRequestHelper;
import ru.extoozy.validators.ValidationResult;
import ru.extoozy.validators.jsonvalidator.JsonUserProfileValidator;
import ru.extoozy.web.handler.requesthandler.HttpServletRequestHandler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateProfileRequestHandler implements HttpServletRequestHandler {
    public static final CreateProfileRequestHandler INSTANCE = new CreateProfileRequestHandler();

    private final UserProfileService userProfileService = ApplicationContext.getBean(UserProfileService.class);

    @Override
    public JsonResponse handleRequest(HttpServletRequest req) throws Exception {
        var jsonMap = ServletRequestHelper.getJsonMap(req);
        var jsonResponse = new JsonResponse();
        ValidationResult validationResult = JsonUserProfileValidator.validateCreateUserProfileJson(jsonMap);
        if (validationResult.isValid()) {
            CreateUserProfileDto dto = UserProfileMapper.INSTANCE.toCreateUserProfileDto(jsonMap);
            userProfileService.create(dto);
        } else {
            jsonResponse.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addAllErrors(validationResult.errors());
        }
        return jsonResponse;
    }
}
