package ru.extoozy.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.extoozy.config.DbConfig;
import ru.extoozy.listener.ApplicationContextInitializer;
import ru.extoozy.migration.MigrationTool;
import ru.extoozy.repository.user.UserRepository;
import ru.extoozy.repository.user.impl.JdbcUserRepository;
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

class RegisterServletTest {
    private RegisterServlet registerServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter stringWriter;
    private Connection connection;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {
        new ApplicationContextInitializer().contextInitialized(null);
        registerServlet = new RegisterServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        PrintWriter printStream = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printStream);
        DbConfig dbConfig = new DbConfig();
        connection = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
        MigrationTool.runMigrate();
        userRepository = new JdbcUserRepository();
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    @DisplayName("POST должен создать пользователя при корректных данных")
    void doPost_whenValid_shouldCreateUser() throws Exception {
        String json = """
                {
                    "email": "ilya.makarov.04@mail.ru",
                    "password": "password"
                }
                """;
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(jsonReader);
        registerServlet.doPost(new CachedBodyHttpServletRequest(request), response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThat(jsonResponse).containsEntry("status_code", 200);
        assertThat(userRepository.getByEmail("ilya.makarov.04@mail.ru")).isNotNull();
    }

    @Test
    @DisplayName("POST должен вернуть ошибку при некорректном email")
    void doPost_whenInvalidEmail_shouldReturnInvalidEmailResponse() throws Exception {
        String json = """
                {
                    "email": "mail",
                    "password": "password"
                }
                """;
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getReader()).thenReturn(jsonReader);
        registerServlet.doPost(new CachedBodyHttpServletRequest(request), response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThat(jsonResponse).containsEntry("status_code", 400);
        assertThat(jsonResponse).containsEntry("errors", List.of("Email is mail invalid"));

    }

}