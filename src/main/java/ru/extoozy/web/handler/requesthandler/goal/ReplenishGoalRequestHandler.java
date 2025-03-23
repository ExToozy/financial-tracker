package ru.extoozy.web.handler.requesthandler.goal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.context.ApplicationContext;
import ru.extoozy.dto.goal.ReplenishGoalDto;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.mapper.GoalMapper;
import ru.extoozy.service.goal.GoalService;
import ru.extoozy.util.JsonResponse;
import ru.extoozy.util.ServletRequestHelper;
import ru.extoozy.validators.ValidationResult;
import ru.extoozy.validators.jsonvalidator.JsonGoalValidator;
import ru.extoozy.web.handler.requesthandler.HttpServletRequestHandler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReplenishGoalRequestHandler implements HttpServletRequestHandler {
    public static final ReplenishGoalRequestHandler INSTANCE = new ReplenishGoalRequestHandler();

    private final GoalService goalService = ApplicationContext.getBean(GoalService.class);

    @Override
    public JsonResponse handleRequest(HttpServletRequest req) throws Exception {
        var jsonMap = ServletRequestHelper.getJsonMap(req);
        var jsonResponse = new JsonResponse();
        ValidationResult validationResult = JsonGoalValidator.validateReplenishGoalJson(jsonMap);
        if (validationResult.isValid()) {
            ReplenishGoalDto dto = GoalMapper.INSTANCE.toReplenishGoalDto(jsonMap);
            try {
                goalService.replenish(dto.getGoalId(), dto.getAmount());
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
