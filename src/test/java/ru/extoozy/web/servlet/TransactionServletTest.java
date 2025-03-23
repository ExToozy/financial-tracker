package ru.extoozy.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.extoozy.config.DbConfig;
import ru.extoozy.context.UserContext;
import ru.extoozy.entity.TransactionEntity;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.enums.TransactionType;
import ru.extoozy.enums.UserRole;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.listener.ApplicationContextInitializer;
import ru.extoozy.migration.MigrationTool;
import ru.extoozy.repository.profile.impl.JdbcUserProfileRepository;
import ru.extoozy.repository.transaction.TransactionRepository;
import ru.extoozy.repository.transaction.impl.JdbcTransactionRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransactionServletTest {
    private TransactionServlet transactionServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter stringWriter;
    private TransactionRepository transactionRepository;
    private Connection connection;

    private UserEntity user;

    @BeforeEach
    void setUp() throws Exception {
        new ApplicationContextInitializer().contextInitialized(null);
        transactionServlet = new TransactionServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        PrintWriter printStream = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printStream);
        DbConfig dbConfig = new DbConfig();
        connection = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
        MigrationTool.runMigrate();
        transactionRepository = new JdbcTransactionRepository();
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
    @DisplayName("GET должен вернуть пустой список, если транзакций нет")
    void doGet_shouldReturnEmptyList_whenTransactionsNotExist() throws Exception {
        String authHeader = "Bearer " + user.getId();
        String json = """
                {
                    "user_profile_id": %s
                }
                """.formatted(user.getUserProfile().getId());
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getReader()).thenReturn(jsonReader);

        transactionServlet.doGet(request, response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThat(jsonResponse).containsEntry("status_code", 200);
        assertThat((Map) jsonResponse.get("data")).containsEntry("transactions", new ArrayList<>());
    }

    @Test
    @DisplayName("POST должен создать транзакцию")
    void doPost() throws Exception {
        String authHeader = "Bearer " + user.getId();
        String json = """
                {
                    "category": "Одежда",
                    "description": "Покупка одежды",
                    "transaction_type": "%s",
                    "amount": 500.0
                }
                """.formatted(TransactionType.WITHDRAWAL.toString());
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getReader()).thenReturn(jsonReader);

        transactionServlet.doPost(request, response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThat(jsonResponse).containsEntry("status_code", 200);
        List<TransactionEntity> transactions = transactionRepository.findAllByUserProfileId(user.getUserProfile().getId());
        assertThat(transactions).hasSize(1);
    }

    @Test
    @DisplayName("PUT должен обновить существующую транзакцию")
    void doPut() throws Exception {
        TransactionEntity transaction = TransactionEntity.builder()
                .transactionType(TransactionType.REPLENISHMENT)
                .category("Одежда")
                .description("Покупка одежды")
                .createdAt(LocalDateTime.now())
                .userProfile(user.getUserProfile())
                .amount(BigDecimal.TEN)
                .build();
        transactionRepository.save(transaction);

        String authHeader = "Bearer " + user.getId();
        String json = """
                {
                    "transaction_id": %s,
                    "category": "Новая категория",
                    "description": "Новое описание",
                    "amount": 500.0
                }
                """.formatted(transaction.getId());
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getReader()).thenReturn(jsonReader);

        transactionServlet.doPut(request, response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        TransactionEntity updatedTransaction = transactionRepository.findById(transaction.getId());
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(jsonResponse).containsEntry("status_code", 200);
        softly.assertThat(updatedTransaction.getCategory()).isEqualTo("Новая категория");
        softly.assertThat(updatedTransaction.getDescription()).isEqualTo("Новое описание");
        softly.assertThat(updatedTransaction.getAmount())
                .usingComparator(BigDecimal::compareTo)
                .isEqualTo(BigDecimal.valueOf(500));
        softly.assertAll();

    }

    @Test
    @DisplayName("DELETE должен удалить существующую транзакцию")
    void doDelete() throws Exception {
        TransactionEntity transaction = TransactionEntity.builder()
                .transactionType(TransactionType.REPLENISHMENT)
                .category("Одежда")
                .description("Покупка одежды")
                .createdAt(LocalDateTime.now())
                .userProfile(user.getUserProfile())
                .amount(BigDecimal.TEN)
                .build();
        transactionRepository.save(transaction);

        String authHeader = "Bearer " + user.getId();
        String json = """
                {
                    "transaction_id": %s
                }
                """.formatted(transaction.getId());
        BufferedReader jsonReader = new BufferedReader(new StringReader(json));
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(request.getReader()).thenReturn(jsonReader);

        transactionServlet.doDelete(request, response);

        Map<String, Object> jsonResponse = JsonMapper.getJsonMap(new StringReader(stringWriter.toString()));
        System.out.println(jsonResponse);
        assertThatThrownBy(() -> transactionRepository.findById(transaction.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}