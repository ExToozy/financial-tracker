package ru.extoozy.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.extoozy.constant.ApiEndpointConstants;
import ru.extoozy.web.handler.HttpServletHandler;
import ru.extoozy.web.handler.requesthandler.user.DeleteUserRequestHandler;
import ru.extoozy.web.handler.requesthandler.user.GetUserRequestHandler;
import ru.extoozy.web.handler.requesthandler.user.UpdateUserRequestHandler;

import java.io.IOException;

@WebServlet(ApiEndpointConstants.USER_ENDPOINT)
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpServletHandler.handle(req, resp, GetUserRequestHandler.INSTANCE);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpServletHandler.handle(req, resp, UpdateUserRequestHandler.INSTANCE);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpServletHandler.handle(req, resp, DeleteUserRequestHandler.INSTANCE);
    }
}
