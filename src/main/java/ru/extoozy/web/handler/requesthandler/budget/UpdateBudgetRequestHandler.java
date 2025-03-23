package ru.extoozy.web.handler.requesthandler.budget;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.context.ApplicationContext;
import ru.extoozy.dto.budget.UpdateBudgetDto;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.mapper.BudgetMapper;
import ru.extoozy.service.budget.BudgetService;
import ru.extoozy.util.JsonResponse;
import ru.extoozy.util.ServletRequestHelper;
import ru.extoozy.validators.ValidationResult;
import ru.extoozy.validators.jsonvalidator.JsonBudgetValidator;
import ru.extoozy.web.handler.requesthandler.HttpServletRequestHandler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateBudgetRequestHandler implements HttpServletRequestHandler {
    public static final UpdateBudgetRequestHandler INSTANCE = new UpdateBudgetRequestHandler();

    private final BudgetService budgetService = ApplicationContext.getBean(BudgetService.class);

    @Override
    public JsonResponse handleRequest(HttpServletRequest req) throws Exception {
        var jsonMap = ServletRequestHelper.getJsonMap(req);
        var jsonResponse = new JsonResponse();
        ValidationResult validationResult = JsonBudgetValidator.validateUpdateBudgetJson(jsonMap);
        if (validationResult.isValid()) {
            UpdateBudgetDto dto = BudgetMapper.INSTANCE.toUpdateBudgetDto(jsonMap);
            try {
                budgetService.update(dto);
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
