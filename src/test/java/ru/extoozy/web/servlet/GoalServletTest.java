package ru.extoozy.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.extoozy.config.DbConfig;
import ru.extoozy.context.UserContext;
import ru.extoozy.entity.GoalEntity;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.enums.UserRole;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.listener.ApplicationContextInitializer;
import ru.extoozy.migration.MigrationTool;
import ru.extoozy.repository.goal.GoalRepository;
import ru.extoozy.repository.goal.impl.JdbcGoalRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GoalServletTest {
    private GoalServlet goalServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter stringWriter;
    private GoalRepository goalRepository;
    private Connection connection;

    private UserEntity user;

    @BeforeEach
    void setUp() throws Exception {
        new ApplicationContextInitializer().contextInitialized(null);
        goalServlet = new GoalServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        PrintWriter printStream = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printStream);
        DbConfig dbConfig = new DbConfig();
        connection = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
        MigrationTool.runMigrate();
        goalRepository = new JdbcGoalRepository();
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
    @DisplayName("GET должен вернуть пустой список, если целей нет")
    void doGet_shouldReturnEmptyList_whenGoalsNotExists() throws Exception {
        String authHeader = "Bearer " + user.getId();
        String json = """
                {
                    "user_profile_id": %s
                }
                """.formatted(user.getUserProfile().getId());
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getReader()).thenReturn(jsonReader);

        goalServlet.doGet(request, response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThat(jsonResponse).containsEntry("status_code", 200);
        assertThat((Map) jsonResponse.get("data")).containsEntry("goals", new ArrayList<>());
    }

    @Test
    @DisplayName("GET должен вернуть список целей, если они существуют")
    void doGet_shouldReturnGoals_whenGoalExists() throws Exception {
        goalRepository.save(
                GoalEntity.builder()
                        .currentAmount(BigDecimal.ZERO)
                        .goalAmount(BigDecimal.ONE)
                        .name("Test goal")
                        .userProfile(user.getUserProfile())
                        .build()
        );
        String authHeader = "Bearer " + user.getId();
        String json = """
                {
                    "user_profile_id": %s
                }
                """.formatted(user.getUserProfile().getId());
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getReader()).thenReturn(jsonReader);

        goalServlet.doGet(request, response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThat(jsonResponse).containsEntry("status_code", 200);
        assertThat((List) ((Map) jsonResponse.get("data")).get("goals")).hasSize(1);
    }

    @Test
    @DisplayName("POST должен создать цель при корректном запросе")
    void doPost_shouldCreateGoal_whenValidRequest() throws Exception {
        String authHeader = "Bearer " + user.getId();
        String json = """
                {
                    "name": "Test Goal",
                    "goal_amount": 500.0
                }
                """;
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getReader()).thenReturn(jsonReader);

        goalServlet.doPost(request, response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThat(jsonResponse).containsEntry("status_code", 200);
        List<GoalEntity> goals = goalRepository.findAllByUserProfileId(user.getUserProfile().getId());
        assertThat(goals).hasSize(1);
    }

    @Test
    @DisplayName("PUT должен пополнить цель при корректном запросе")
    void doPut_shouldReplenishGoal_whenValidRequest() throws Exception {
        GoalEntity goal = GoalEntity.builder()
                .currentAmount(BigDecimal.ZERO)
                .goalAmount(BigDecimal.valueOf(500))
                .name("Test goal")
                .userProfile(user.getUserProfile())
                .build();
        goalRepository.save(goal);
        String authHeader = "Bearer " + user.getId();
        String json = """
                {
                    "goal_id": %s,
                    "amount": 499.0
                }
                """.formatted(goal.getId());
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getReader()).thenReturn(jsonReader);

        goalServlet.doPut(request, response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThat(jsonResponse).containsEntry("status_code", 200);
        GoalEntity updatedGoal = goalRepository.findById(goal.getId());
        assertThat(updatedGoal.getCurrentAmount())
                .usingComparator(BigDecimal::compareTo)
                .isEqualTo(BigDecimal.valueOf(499));
    }

    @Test
    @DisplayName("DELETE должен удалить цель при корректном запросе")
    void doDelete_shouldDeleteGoal_whenValidRequest() throws Exception {
        GoalEntity goal = GoalEntity.builder()
                .currentAmount(BigDecimal.ZERO)
                .goalAmount(BigDecimal.valueOf(500))
                .name("Test goal")
                .userProfile(user.getUserProfile())
                .build();
        goalRepository.save(goal);
        String authHeader = "Bearer " + user.getId();
        String json = """
                {
                    "goal_id": %s
                }
                """.formatted(goal.getId());
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getReader()).thenReturn(jsonReader);

        goalServlet.doDelete(request, response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThat(jsonResponse).containsEntry("status_code", 200);
        assertThatThrownBy(() -> goalRepository.findById(goal.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}