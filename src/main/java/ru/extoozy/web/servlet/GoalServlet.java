package ru.extoozy.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.extoozy.constant.ApiEndpointConstants;
import ru.extoozy.web.handler.HttpServletHandler;
import ru.extoozy.web.handler.requesthandler.goal.CreateGoalRequestHandler;
import ru.extoozy.web.handler.requesthandler.goal.DeleteGoalRequestHandler;
import ru.extoozy.web.handler.requesthandler.goal.GetGoalsRequestHandler;
import ru.extoozy.web.handler.requesthandler.goal.ReplenishGoalRequestHandler;

import java.io.IOException;

@WebServlet(ApiEndpointConstants.GOAL_ENDPOINT)
public class GoalServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpServletHandler.handle(req, resp, GetGoalsRequestHandler.INSTANCE);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpServletHandler.handle(req, resp, CreateGoalRequestHandler.INSTANCE);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpServletHandler.handle(req, resp, ReplenishGoalRequestHandler.INSTANCE);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpServletHandler.handle(req, resp, DeleteGoalRequestHandler.INSTANCE);
    }
}
