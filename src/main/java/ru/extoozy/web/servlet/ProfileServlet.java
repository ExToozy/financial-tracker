package ru.extoozy.web.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.extoozy.constant.ApiEndpointConstants;
import ru.extoozy.web.handler.HttpServletHandler;
import ru.extoozy.web.handler.requesthandler.profile.CreateProfileRequestHandler;
import ru.extoozy.web.handler.requesthandler.profile.DeleteProfileRequestHandler;
import ru.extoozy.web.handler.requesthandler.profile.GetProfileRequestHandler;
import ru.extoozy.web.handler.requesthandler.profile.UpdateProfileRequestHandler;

import java.io.IOException;

@WebServlet(ApiEndpointConstants.PROFILE_ENDPOINT)
public class ProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpServletHandler.handle(req, resp, GetProfileRequestHandler.INSTANCE);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpServletHandler.handle(req, resp, CreateProfileRequestHandler.INSTANCE);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpServletHandler.handle(req, resp, UpdateProfileRequestHandler.INSTANCE);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpServletHandler.handle(req, resp, DeleteProfileRequestHandler.INSTANCE);
    }
}
