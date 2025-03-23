package ru.extoozy.web.handler.requesthandler.budget;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.context.ApplicationContext;
import ru.extoozy.dto.budget.CreateBudgetDto;
import ru.extoozy.mapper.BudgetMapper;
import ru.extoozy.service.budget.BudgetService;
import ru.extoozy.util.JsonResponse;
import ru.extoozy.util.ServletRequestHelper;
import ru.extoozy.validators.ValidationResult;
import ru.extoozy.validators.jsonvalidator.JsonBudgetValidator;
import ru.extoozy.web.handler.requesthandler.HttpServletRequestHandler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateBudgetRequestHandler implements HttpServletRequestHandler {
    public static final CreateBudgetRequestHandler INSTANCE = new CreateBudgetRequestHandler();

    private final BudgetService budgetService = ApplicationContext.getBean(BudgetService.class);

    @Override
    public JsonResponse handleRequest(HttpServletRequest req) throws Exception {
        var jsonMap = ServletRequestHelper.getJsonMap(req);
        var jsonResponse = new JsonResponse();
        ValidationResult validationResult = JsonBudgetValidator.validateCreateBudgetJson(jsonMap);
        if (validationResult.isValid()) {
            CreateBudgetDto dto = BudgetMapper.INSTANCE.toCreateBudgetDto(jsonMap);
            budgetService.create(dto);
        } else {
            jsonResponse.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addAllErrors(validationResult.errors());
        }
        return jsonResponse;
    }
}
