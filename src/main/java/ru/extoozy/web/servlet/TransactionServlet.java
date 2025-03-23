package ru.extoozy.web.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.extoozy.constant.ApiEndpointConstants;
import ru.extoozy.web.handler.HttpServletHandler;
import ru.extoozy.web.handler.requesthandler.transaction.CreateTransactionRequestHandler;
import ru.extoozy.web.handler.requesthandler.transaction.DeleteTransactionRequestHandler;
import ru.extoozy.web.handler.requesthandler.transaction.GetTransactionsRequestHandler;
import ru.extoozy.web.handler.requesthandler.transaction.UpdateTransactionRequestHandler;

import java.io.IOException;

@WebServlet(ApiEndpointConstants.TRANSACTION_ENDPOINT)
public class TransactionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpServletHandler.handle(req, resp, GetTransactionsRequestHandler.INSTANCE);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpServletHandler.handle(req, resp, CreateTransactionRequestHandler.INSTANCE);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpServletHandler.handle(req, resp, UpdateTransactionRequestHandler.INSTANCE);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpServletHandler.handle(req, resp, DeleteTransactionRequestHandler.INSTANCE);
    }
}
