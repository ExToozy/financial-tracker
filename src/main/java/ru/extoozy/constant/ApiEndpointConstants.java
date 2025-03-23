package ru.extoozy.constant;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiEndpointConstants {
    public static final String ALL_ENDPOINTS = "/api/*";
    public static final String AUTHORIZATION_ENDPOINT = "/api/v1/login";
    public static final String[] AUTHORIZATION_ENDPOINT_METHODS = new String[]{"POST"};

    public static final String REGISTRATION_ENDPOINT = "/api/v1/register";
    public static final String[] REGISTRATION_ENDPOINT_METHODS = new String[]{"POST"};

    public static final String BUDGET_ENDPOINT = "/api/v1/budgets";
    public static final String[] BUDGET_ENDPOINT_METHODS = new String[]{"GET", "POST"};

    public static final String GOAL_ENDPOINT = "/api/v1/goals";
    public static final String[] GOAL_ENDPOINT_METHODS = new String[]{"GET", "POST", "PUT", "DELETE"};

    public static final String PROFILE_ENDPOINT = "/api/v1/profiles";
    public static final String[] PROFILE_ENDPOINT_METHODS = new String[]{"GET", "POST", "PUT", "DELETE"};
    public static final String TRANSACTION_ENDPOINT = "/api/v1/transactions";
    public static final String[] TRANSACTION_ENDPOINT_METHODS = new String[]{"GET", "POST", "PUT", "DELETE"};
    public static final String USER_ENDPOINT = "/api/v1/users";
    public static final String[] USER_ENDPOINT_METHODS = new String[]{"GET", "PUT", "DELETE"};

}
