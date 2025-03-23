package ru.extoozy.web.handler.requesthandler.goal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.context.ApplicationContext;
import ru.extoozy.dto.goal.CreateGoalDto;
import ru.extoozy.mapper.GoalMapper;
import ru.extoozy.service.goal.GoalService;
import ru.extoozy.util.JsonResponse;
import ru.extoozy.util.ServletRequestHelper;
import ru.extoozy.validators.ValidationResult;
import ru.extoozy.validators.jsonvalidator.JsonGoalValidator;
import ru.extoozy.web.handler.requesthandler.HttpServletRequestHandler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateGoalRequestHandler implements HttpServletRequestHandler {
    public static final CreateGoalRequestHandler INSTANCE = new CreateGoalRequestHandler();

    private final GoalService goalService = ApplicationContext.getBean(GoalService.class);

    @Override
    public JsonResponse handleRequest(HttpServletRequest req) throws Exception {
        var jsonMap = ServletRequestHelper.getJsonMap(req);
        var jsonResponse = new JsonResponse();
        ValidationResult validationResult = JsonGoalValidator.validateCreateGoalJson(jsonMap);
        if (validationResult.isValid()) {
            CreateGoalDto dto = GoalMapper.INSTANCE.toCreateGoalDto(jsonMap);
            goalService.create(dto);
        } else {
            jsonResponse.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addAllErrors(validationResult.errors());
        }
        return jsonResponse;
    }
}
