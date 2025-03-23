package ru.extoozy.web.handler.requesthandler.budget;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.context.ApplicationContext;
import ru.extoozy.dto.budget.BudgetDto;
import ru.extoozy.dto.budget.GetBudgetDto;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.mapper.BudgetMapper;
import ru.extoozy.service.budget.BudgetService;
import ru.extoozy.util.JsonResponse;
import ru.extoozy.util.ServletRequestHelper;
import ru.extoozy.validators.ValidationResult;
import ru.extoozy.validators.jsonvalidator.JsonBudgetValidator;
import ru.extoozy.web.handler.requesthandler.HttpServletRequestHandler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetBudgetRequestHandler implements HttpServletRequestHandler {
    public static final GetBudgetRequestHandler INSTANCE = new GetBudgetRequestHandler();

    private final BudgetService budgetService = ApplicationContext.getBean(BudgetService.class);

    @Override
    public JsonResponse handleRequest(HttpServletRequest req) throws Exception {
        var jsonMap = ServletRequestHelper.getJsonMap(req);
        var jsonResponse = new JsonResponse();
        ValidationResult validationResult = JsonBudgetValidator.validateGetBudgetJson(jsonMap);
        if (validationResult.isValid()) {
            GetBudgetDto dto = BudgetMapper.INSTANCE.toGetBudgetDto(jsonMap);
            try {
                BudgetDto budget = budgetService.getByUserProfileIdAndCurrentMonth(dto.getUserProfileId());
                jsonResponse.putDataEntry("budget", budget);
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
