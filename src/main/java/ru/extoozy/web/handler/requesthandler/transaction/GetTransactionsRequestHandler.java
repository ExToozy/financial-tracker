package ru.extoozy.web.handler.requesthandler.transaction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.context.ApplicationContext;
import ru.extoozy.dto.transaction.GetTransactionsDto;
import ru.extoozy.dto.transaction.TransactionDto;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.mapper.TransactionMapper;
import ru.extoozy.service.transaction.TransactionService;
import ru.extoozy.util.JsonResponse;
import ru.extoozy.util.ServletRequestHelper;
import ru.extoozy.validators.ValidationResult;
import ru.extoozy.validators.jsonvalidator.JsonTransactionValidator;
import ru.extoozy.web.handler.requesthandler.HttpServletRequestHandler;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetTransactionsRequestHandler implements HttpServletRequestHandler {
    public static final GetTransactionsRequestHandler INSTANCE = new GetTransactionsRequestHandler();

    private final TransactionService transactionService = ApplicationContext.getBean(TransactionService.class);

    @Override
    public JsonResponse handleRequest(HttpServletRequest req) throws Exception {
        var jsonMap = ServletRequestHelper.getJsonMap(req);
        var jsonResponse = new JsonResponse();
        ValidationResult validationResult = JsonTransactionValidator.validateGetTransactionsJson(jsonMap);
        if (validationResult.isValid()) {
            GetTransactionsDto dto = TransactionMapper.INSTANCE.toGetTransactionsDto(jsonMap);
            try {
                List<TransactionDto> transactions = transactionService.getAllByUserProfileId(dto.getUserProfileId());
                jsonResponse.putDataEntry("transactions", transactions);
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
