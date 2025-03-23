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
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.listener.ApplicationContextInitializer;
import ru.extoozy.migration.MigrationTool;
import ru.extoozy.repository.profile.UserProfileRepository;
import ru.extoozy.repository.profile.impl.JdbcUserProfileRepository;
import ru.extoozy.repository.user.impl.JdbcUserRepository;
import ru.extoozy.util.JsonMapper;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProfileServletTest {

    private ProfileServlet profileServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter stringWriter;
    private UserProfileRepository userProfileRepository;
    private Connection connection;

    private UserEntity user;

    @BeforeEach
    void setUp() throws Exception {
        new ApplicationContextInitializer().contextInitialized(null);
        profileServlet = new ProfileServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        PrintWriter printStream = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printStream);
        DbConfig dbConfig = new DbConfig();
        connection = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
        MigrationTool.runMigrate();
        userProfileRepository = new JdbcUserProfileRepository();
        user = UserEntity.builder()
                .email("ilya.makarov.04@mail.ru")
                .role(UserRole.USER)
                .password("test")
                .build();
        JdbcUserRepository jdbcUserRepository = new JdbcUserRepository();
        jdbcUserRepository.save(user);
        UserContext.setUser(jdbcUserRepository.findById(user.getId()));
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    @DisplayName("GET должен вернуть 404, если профиль не найден")
    void doGet_shouldReturn404_whenProfileNotExists() throws Exception {
        String authHeader = "Bearer " + user.getId();
        String json = """
                {
                    "user_id": %s
                }
                """.formatted(user.getId());
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getReader()).thenReturn(jsonReader);

        profileServlet.doGet(request, response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThat(jsonResponse).containsEntry("status_code", 404);
    }

    @Test
    @DisplayName("GET должен вернуть профиль, если он существует")
    void doGet_shouldReturnProfile_whenProfileExist() throws Exception {
        UserProfileEntity userProfile = UserProfileEntity.builder()
                .firstName("first")
                .lastName("last")
                .user(user)
                .build();
        userProfileRepository.save(
                userProfile
        );
        UserContext.getUser().setUserProfile(userProfile);
        String authHeader = "Bearer " + user.getId();
        String json = """
                {
                    "user_id": %s
                }
                """.formatted(user.getId());
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getReader()).thenReturn(jsonReader);

        profileServlet.doGet(request, response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThat(jsonResponse).containsEntry("status_code", 200);
        assertThat(((Map) jsonResponse.get("data")).containsKey("profile"));
    }

    @Test
    @DisplayName("POST должен создать профиль при корректном запросе")
    void doPost_shouldCreateProfile_whenValidRequest() throws Exception {
        String authHeader = "Bearer " + user.getId();
        String json = """
                {
                    "firstname": "first",
                    "lastname": "last"
                }
                """;
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getReader()).thenReturn(jsonReader);

        profileServlet.doPost(request, response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThat(jsonResponse).containsEntry("status_code", 200);
        UserProfileEntity profile = userProfileRepository.findByUserId(user.getId());
        assertThat(profile.getLastName()).isEqualTo("last");
        assertThat(profile.getFirstName()).isEqualTo("first");
    }

    @Test
    @DisplayName("PUT должен обновить профиль при корректном запросе")
    void doPut_shouldUpdateProfile_whenValidRequest() throws Exception {
        UserProfileEntity userProfile = UserProfileEntity.builder()
                .firstName("first")
                .lastName("last")
                .user(user)
                .build();
        UserContext.getUser().setUserProfile(userProfile);
        userProfileRepository.save(userProfile);
        String authHeader = "Bearer " + user.getId();
        String json = """
                {
                    "user_profile_id": %s,
                    "firstname": "updatedFirst",
                    "lastname": "updatedLast"
                }
                """.formatted(userProfile.getId());
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getReader()).thenReturn(jsonReader);

        profileServlet.doPut(request, response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThat(jsonResponse).containsEntry("status_code", 200);
        UserProfileEntity profile = userProfileRepository.findByUserId(UserContext.getUser().getId());
        assertThat(profile.getFirstName())
                .isEqualTo("updatedFirst");
        assertThat(profile.getLastName())
                .isEqualTo("updatedLast");
    }

    @Test
    @DisplayName("DELETE должен удалить профиль при корректном запросе")
    void doDelete_shouldDeleteGoal_whenValidRequest() throws Exception {
        UserProfileEntity userProfile = UserProfileEntity.builder()
                .firstName("first")
                .lastName("last")
                .user(user)
                .build();
        UserContext.getUser().setUserProfile(userProfile);
        userProfileRepository.save(userProfile);
        String authHeader = "Bearer " + user.getId();
        String json = """
                {
                    "user_profile_id": %s
                }
                """.formatted(userProfile.getId());
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getReader()).thenReturn(jsonReader);

        profileServlet.doDelete(request, response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThat(jsonResponse).containsEntry("status_code", 200);
        assertThatThrownBy(() -> userProfileRepository.findById(userProfile.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}