package ru.extoozy.web.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.extoozy.constant.ApiEndpointConstants;
import ru.extoozy.web.handler.HttpServletHandler;
import ru.extoozy.web.handler.requesthandler.auth.RegisterRequestHandler;

import java.io.IOException;

@WebServlet(ApiEndpointConstants.REGISTRATION_ENDPOINT)
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpServletHandler.handle(req, resp, RegisterRequestHandler.INSTANCE);
    }
}
