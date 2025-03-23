package ru.extoozy.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.extoozy.config.DbConfig;
import ru.extoozy.context.UserContext;
import ru.extoozy.entity.BudgetEntity;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.enums.UserRole;
import ru.extoozy.listener.ApplicationContextInitializer;
import ru.extoozy.migration.MigrationTool;
import ru.extoozy.repository.budget.BudgetRepository;
import ru.extoozy.repository.budget.impl.JdbcBudgetRepository;
import ru.extoozy.repository.profile.impl.JdbcUserProfileRepository;
import ru.extoozy.repository.user.impl.JdbcUserRepository;
import ru.extoozy.util.JsonMapper;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BudgetServletTest {

    private BudgetServlet budgetServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter stringWriter;
    private BudgetRepository budgetRepository;
    private Connection connection;

    private UserEntity user;

    @BeforeEach
    void setUp() throws Exception {
        new ApplicationContextInitializer().contextInitialized(null);
        budgetServlet = new BudgetServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        PrintWriter printStream = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printStream);
        DbConfig dbConfig = new DbConfig();
        connection = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
        MigrationTool.runMigrate();
        budgetRepository = new JdbcBudgetRepository();
        user = UserEntity.builder()
                .email("ilya.makarov.04@mail.ru")
                .role(UserRole.USER)
                .password("test")
                .build();
        UserProfileEntity userProfile = UserProfileEntity.builder()
                .firstName("Ilya")
                .lastName("Makarov")
                .user(user)
                .build();
        new JdbcUserRepository().save(user);
        new JdbcUserProfileRepository().save(userProfile);
        user.setUserProfile(userProfile);
        UserContext.setUser(user);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    @DisplayName("doGet должен вернуть 404 когда бюджет не создан")
    void doGet_shouldReturn404_whenBudgetNotExists() throws Exception {
        String authHeader = "Bearer " + user.getId();
        String json = """
                {
                    "user_profile_id": %s
                }
                """.formatted(user.getUserProfile().getId());
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getReader()).thenReturn(jsonReader);

        budgetServlet.doGet(request, response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThat(jsonResponse).containsEntry("status_code", 404);
    }

    @Test
    @DisplayName("doGet должен вернуть бюджет и статус OK")
    void doGet_shouldReturn200AndBudget_whenBudgetExists() throws Exception {
        budgetRepository.save(BudgetEntity.builder()
                .maxAmount(BigDecimal.TEN)
                .currentAmount(BigDecimal.ZERO)
                .userProfile(user.getUserProfile())
                .period(YearMonth.now())
                .build());
        String authHeader = "Bearer " + user.getId();
        String json = """
                {
                    "user_profile_id": %s
                }
                """.formatted(user.getUserProfile().getId());
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getReader()).thenReturn(jsonReader);

        budgetServlet.doGet(request, response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThat(jsonResponse).containsEntry("status_code", 200);
    }

    @Test
    @DisplayName("doPost должен создать бюджет и вернуть статус Created")
    void doPost_shouldCreateBudgetAndReturnCreated() throws Exception {
        String authHeader = "Bearer " + user.getId();
        String json = """
                {
                    "max_amount": %s
                }
                """.formatted(500);
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getReader()).thenReturn(jsonReader);

        budgetServlet.doPost(request, response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThat(jsonResponse).containsEntry("status_code", 200);
    }

    @Test
    @DisplayName("doPut должен обновить бюджет и вернуть статус OK")
    void doPut_shouldUpdateBudgetAndReturnOk() throws Exception {
        BudgetEntity budget = BudgetEntity.builder()
                .maxAmount(BigDecimal.TEN)
                .currentAmount(BigDecimal.ZERO)
                .userProfile(user.getUserProfile())
                .period(YearMonth.now())
                .build();
        budgetRepository.save(budget);
        String authHeader = "Bearer " + user.getId();
        String json = """
                {
                    "budget_id": %s,
                    "max_amount": %s
                }
                """.formatted(budget.getId(), 200.0);
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getReader()).thenReturn(jsonReader);

        budgetServlet.doPut(request, response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);

        BudgetEntity updatedBudget = budgetRepository.findById(budget.getId());

        assertThat(jsonResponse).containsEntry("status_code", 200);
        assertThat(updatedBudget.getMaxAmount())
                .usingComparator(BigDecimal::compareTo)
                .isEqualTo(BigDecimal.valueOf(200));
    }
}
