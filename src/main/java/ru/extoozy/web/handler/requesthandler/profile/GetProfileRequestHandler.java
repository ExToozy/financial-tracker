package ru.extoozy.web.handler.requesthandler.profile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.context.ApplicationContext;
import ru.extoozy.dto.profile.GetUserProfileDto;
import ru.extoozy.dto.profile.UserProfileDto;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.mapper.UserProfileMapper;
import ru.extoozy.service.profile.UserProfileService;
import ru.extoozy.util.JsonResponse;
import ru.extoozy.util.ServletRequestHelper;
import ru.extoozy.validators.ValidationResult;
import ru.extoozy.validators.jsonvalidator.JsonUserProfileValidator;
import ru.extoozy.web.handler.requesthandler.HttpServletRequestHandler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetProfileRequestHandler implements HttpServletRequestHandler {
    public static final GetProfileRequestHandler INSTANCE = new GetProfileRequestHandler();

    private final UserProfileService userProfileService = ApplicationContext.getBean(UserProfileService.class);

    @Override
    public JsonResponse handleRequest(HttpServletRequest req) throws Exception {
        var jsonMap = ServletRequestHelper.getJsonMap(req);
        var jsonResponse = new JsonResponse();
        ValidationResult validationResult = JsonUserProfileValidator.validateGetUserProfileJson(jsonMap);
        if (validationResult.isValid()) {
            GetUserProfileDto dto = UserProfileMapper.INSTANCE.toGetUserProfileDto(jsonMap);
            try {
                UserProfileDto userProfileDto = userProfileService.getByUserId(dto.getUserId());
                jsonResponse.putDataEntry("profile", userProfileDto);
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
