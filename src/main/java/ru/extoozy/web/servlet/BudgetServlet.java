package ru.extoozy.web.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.extoozy.constant.ApiEndpointConstants;
import ru.extoozy.web.handler.HttpServletHandler;
import ru.extoozy.web.handler.requesthandler.budget.CreateBudgetRequestHandler;
import ru.extoozy.web.handler.requesthandler.budget.GetBudgetRequestHandler;
import ru.extoozy.web.handler.requesthandler.budget.UpdateBudgetRequestHandler;

import java.io.IOException;

@WebServlet(ApiEndpointConstants.BUDGET_ENDPOINT)
public class BudgetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpServletHandler.handle(req, resp, GetBudgetRequestHandler.INSTANCE);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpServletHandler.handle(req, resp, CreateBudgetRequestHandler.INSTANCE);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpServletHandler.handle(req, resp, UpdateBudgetRequestHandler.INSTANCE);
    }
}
