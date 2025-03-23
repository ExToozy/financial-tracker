package ru.extoozy.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.extoozy.config.DbConfig;
import ru.extoozy.context.UserContext;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.enums.UserRole;
import ru.extoozy.listener.ApplicationContextInitializer;
import ru.extoozy.migration.MigrationTool;
import ru.extoozy.repository.profile.impl.JdbcUserProfileRepository;
import ru.extoozy.repository.user.impl.JdbcUserRepository;
import ru.extoozy.security.PasswordHelper;
import ru.extoozy.util.CachedBodyHttpServletRequest;
import ru.extoozy.util.JsonMapper;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoginServletTest {
    private LoginServlet loginServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter stringWriter;
    private Connection connection;

    private UserEntity user;

    @BeforeEach
    void setUp() throws Exception {
        new ApplicationContextInitializer().contextInitialized(null);
        loginServlet = new LoginServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        PrintWriter printStream = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printStream);
        DbConfig dbConfig = new DbConfig();
        connection = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
        MigrationTool.runMigrate();
        user = UserEntity.builder()
                .email("ilya.makarov.04@mail.ru")
                .role(UserRole.USER)
                .password(PasswordHelper.getPasswordHash("password"))
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
    @DisplayName("POST должен вернуть токен при корректных учетных данных")
    void doPost_whenValidCredentials_shouldReturnToken() throws Exception {
        String json = """
                {
                    "email": "ilya.makarov.04@mail.ru",
                    "password": "password"
                }
                """;
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(jsonReader);
        loginServlet.doPost(new CachedBodyHttpServletRequest(request), response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThat(jsonResponse).containsEntry("status_code", 200);
        assertThat(((Map) jsonResponse.get("data"))).containsEntry("token", user.getId().toString());
    }

    @Test
    @DisplayName("POST должен вернуть ошибку авторизации при неверных учетных данных")
    void doPost_whenInvalidCredentials_shouldReturnUnauthorizedResponse() throws Exception {
        String json = """
                {
                    "email": "ilya.makarov.04@mail.ru",
                    "password": "password1"
                }
                """;
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(jsonReader);
        loginServlet.doPost(new CachedBodyHttpServletRequest(request), response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThat(jsonResponse).containsEntry("status_code", 401);
        assertThat(jsonResponse).containsEntry("errors", List.of("Wrong password"));
    }
}