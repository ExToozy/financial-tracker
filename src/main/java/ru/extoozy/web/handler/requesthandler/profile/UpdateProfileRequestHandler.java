package ru.extoozy.web.handler.requesthandler.profile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.context.ApplicationContext;
import ru.extoozy.dto.profile.UpdateUserProfileDto;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.mapper.UserProfileMapper;
import ru.extoozy.service.profile.UserProfileService;
import ru.extoozy.util.JsonResponse;
import ru.extoozy.util.ServletRequestHelper;
import ru.extoozy.validators.ValidationResult;
import ru.extoozy.validators.jsonvalidator.JsonUserProfileValidator;
import ru.extoozy.web.handler.requesthandler.HttpServletRequestHandler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateProfileRequestHandler implements HttpServletRequestHandler {
    public static final UpdateProfileRequestHandler INSTANCE = new UpdateProfileRequestHandler();

    private final UserProfileService userProfileService = ApplicationContext.getBean(UserProfileService.class);

    @Override
    public JsonResponse handleRequest(HttpServletRequest req) throws Exception {
        var jsonMap = ServletRequestHelper.getJsonMap(req);
        var jsonResponse = new JsonResponse();
        ValidationResult validationResult = JsonUserProfileValidator.validateUpdateUserProfileJson(jsonMap);
        if (validationResult.isValid()) {
            UpdateUserProfileDto dto = UserProfileMapper.INSTANCE.toUpdateUserProfileDto(jsonMap);
            try {
                userProfileService.update(dto);
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
