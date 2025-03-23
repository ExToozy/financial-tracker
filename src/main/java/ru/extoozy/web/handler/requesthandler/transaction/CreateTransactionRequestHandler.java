package ru.extoozy.web.handler.requesthandler.transaction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.context.ApplicationContext;
import ru.extoozy.dto.transaction.CreateTransactionDto;
import ru.extoozy.mapper.TransactionMapper;
import ru.extoozy.service.transaction.TransactionService;
import ru.extoozy.util.JsonResponse;
import ru.extoozy.util.ServletRequestHelper;
import ru.extoozy.validators.ValidationResult;
import ru.extoozy.validators.jsonvalidator.JsonTransactionValidator;
import ru.extoozy.web.handler.requesthandler.HttpServletRequestHandler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateTransactionRequestHandler implements HttpServletRequestHandler {
    public static final CreateTransactionRequestHandler INSTANCE = new CreateTransactionRequestHandler();

    private final TransactionService transactionService = ApplicationContext.getBean(TransactionService.class);

    @Override
    public JsonResponse handleRequest(HttpServletRequest req) throws Exception {
        var jsonMap = ServletRequestHelper.getJsonMap(req);
        var jsonResponse = new JsonResponse();
        ValidationResult validationResult = JsonTransactionValidator.validateCreateTransactionJson(jsonMap);
        if (validationResult.isValid()) {
            CreateTransactionDto dto = TransactionMapper.INSTANCE.toCreateTransactionDto(jsonMap);
            transactionService.create(dto);
        } else {
            jsonResponse.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.addAllErrors(validationResult.errors());
        }
        return jsonResponse;
    }
}
