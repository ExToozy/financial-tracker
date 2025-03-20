package ru.extoozy.presentation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.extoozy.config.DbConfig;
import ru.extoozy.context.ApplicationContext;
import ru.extoozy.controller.AuthController;
import ru.extoozy.controller.BudgetController;
import ru.extoozy.controller.GoalController;
import ru.extoozy.controller.TransactionController;
import ru.extoozy.controller.TransactionStatisticController;
import ru.extoozy.controller.UserController;
import ru.extoozy.controller.UserProfileController;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.enums.UserRole;
import ru.extoozy.in.ConsoleInHelper;
import ru.extoozy.migration.MigrationTool;
import ru.extoozy.presentation.manager.ActionManager;
import ru.extoozy.presentation.manager.MenuManager;
import ru.extoozy.repository.budget.BudgetRepository;
import ru.extoozy.repository.budget.impl.JdbcBudgetRepository;
import ru.extoozy.repository.goal.GoalRepository;
import ru.extoozy.repository.goal.impl.JdbcGoalRepository;
import ru.extoozy.repository.profile.UserProfileRepository;
import ru.extoozy.repository.profile.impl.JdbcUserProfileRepository;
import ru.extoozy.repository.transaction.TransactionRepository;
import ru.extoozy.repository.transaction.impl.JdbcTransactionRepository;
import ru.extoozy.repository.user.UserRepository;
import ru.extoozy.repository.user.impl.JdbcUserRepository;
import ru.extoozy.security.PasswordHelper;
import ru.extoozy.service.auth.AuthService;
import ru.extoozy.service.auth.impl.AuthServiceImpl;
import ru.extoozy.service.budget.BudgetService;
import ru.extoozy.service.budget.impl.BudgetServiceImpl;
import ru.extoozy.service.email.EmailService;
import ru.extoozy.service.email.impl.ConsoleMockEmailService;
import ru.extoozy.service.goal.GoalService;
import ru.extoozy.service.goal.impl.GoalServiceImpl;
import ru.extoozy.service.profile.UserProfileService;
import ru.extoozy.service.profile.impl.UserProfileServiceImpl;
import ru.extoozy.service.statistic.TransactionStatisticService;
import ru.extoozy.service.statistic.impl.TransactionStatisticServiceImpl;
import ru.extoozy.service.transaction.TransactionService;
import ru.extoozy.service.transaction.impl.TransactionServiceImpl;
import ru.extoozy.service.user.UserService;
import ru.extoozy.service.user.impl.UserServiceImpl;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

class FinancialTrackerTest {

    private static Connection connection;
    private FinancialTracker financialTracker;

    @BeforeAll
    static void runMigration() throws SQLException {
        DbConfig dbConfig = new DbConfig();
        connection = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getUsername());
        MigrationTool.runMigrate();
    }

    @AfterAll
    static void tearDown() throws SQLException {
        connection.close();
    }

    @AfterEach
    void deleteAll() throws SQLException {
        connection.createStatement().execute("DELETE FROM financial_tracker_schema.transactions");
        connection.createStatement().execute("DELETE FROM financial_tracker_schema.goals");
        connection.createStatement().execute("DELETE FROM financial_tracker_schema.budgets");
        connection.createStatement().execute("DELETE FROM financial_tracker_schema.user_profiles");
        connection.createStatement().execute("DELETE FROM financial_tracker_schema.users");
    }

    @BeforeEach
    void setUp() {
        EmailService emailService = new ConsoleMockEmailService();

        BudgetRepository budgetRepository = new JdbcBudgetRepository();
        BudgetService budgetService = new BudgetServiceImpl(budgetRepository);
        BudgetController budgetController = new BudgetController(budgetService);

        UserProfileRepository userProfileRepository = new JdbcUserProfileRepository();
        UserProfileService userProfileService = new UserProfileServiceImpl(userProfileRepository);
        UserProfileController userProfileController = new UserProfileController(userProfileService);

        TransactionRepository transactionRepository = new JdbcTransactionRepository();
        TransactionService transactionService = new TransactionServiceImpl(transactionRepository, budgetRepository, emailService);
        TransactionController transactionController = new TransactionController(transactionService);

        GoalRepository goalRepository = new JdbcGoalRepository();
        GoalService goalService = new GoalServiceImpl(goalRepository);
        GoalController goalController = new GoalController(goalService);

        UserRepository userRepository = new JdbcUserRepository();
        userRepository.save(UserEntity.builder()
                .email("admin")
                .password(PasswordHelper.getPasswordHash("admin"))
                .role(UserRole.ADMIN)
                .build());
        UserService userService = new UserServiceImpl(userRepository);
        UserController userController = new UserController(userService);

        AuthService authService = new AuthServiceImpl(userService, userRepository);
        AuthController authController = new AuthController(authService);

        TransactionStatisticService transactionStatisticService = new TransactionStatisticServiceImpl(transactionRepository);
        TransactionStatisticController transactionStatisticController = new TransactionStatisticController(transactionStatisticService);

        ApplicationContext.addBean(BudgetController.class, budgetController);
        ApplicationContext.addBean(TransactionController.class, transactionController);
        ApplicationContext.addBean(UserProfileController.class, userProfileController);
        ApplicationContext.addBean(GoalController.class, goalController);
        ApplicationContext.addBean(UserController.class, userController);
        ApplicationContext.addBean(AuthController.class, authController);
        ApplicationContext.addBean(TransactionStatisticController.class, transactionStatisticController);

        financialTracker = new FinancialTracker(new ActionManager(), new MenuManager());
    }

    @Test
    void runMainLoop_whenSuccessRegistration_showUserMenu() throws Exception {
        String answers = """
                2
                test@test.ru
                123
                Ilya
                Makarov
                6.1
                нет
                """;
        ByteArrayInputStream in = new ByteArrayInputStream(answers.getBytes());
        Field field = ConsoleInHelper.class.getDeclaredField("SCANNER");
        field.setAccessible(true);
        field.set(null, new Scanner(in));
        financialTracker.runMainLoop();
    }

    @Test
    void runMainLoop_whenSuccessRegistration_userCanCreateReadUpdateDeleteTransaction() throws Exception {
        String answers = """
                2
                test2@test.ru
                123
                Ilya
                Makarov
                2.1
                2
                20000
                Одежда
                Покупка одежды
                2.4
                2.2
                1
                да
                30000
                да
                Продукты
                да
                Покупка еды для дома
                2.3
                1
                6.1
                нет
                """;
        ByteArrayInputStream in = new ByteArrayInputStream(answers.getBytes());
        Field field = ConsoleInHelper.class.getDeclaredField("SCANNER");
        field.setAccessible(true);
        field.set(null, new Scanner(in));
        financialTracker.runMainLoop();
    }

    @Test
    void runMainLoop_whenSuccessRegistration_userCanCreateReadReplenishDeleteGoal() throws Exception {
        String answers = """
                2
                test3@test.ru
                123
                Ilya
                Makarov
                3.1
                Покупка монитора
                15000
                3.3
                3.2
                1
                15000
                3.3
                3.4
                1
                3.3
                6.1
                нет
                """;
        ByteArrayInputStream in = new ByteArrayInputStream(answers.getBytes());
        Field field = ConsoleInHelper.class.getDeclaredField("SCANNER");
        field.setAccessible(true);
        field.set(null, new Scanner(in));
        financialTracker.runMainLoop();
    }

    @Test
    void runMainLoop_whenSuccessRegistration_userCanSetAndUpdateMonthBudget() throws Exception {
        String answers = """
                2
                test4@test.ru
                123
                Ilya
                Makarov
                1.1
                30000
                1.3
                1.2
                40000
                1.3
                6.1
                нет
                """;
        ByteArrayInputStream in = new ByteArrayInputStream(answers.getBytes());
        Field field = ConsoleInHelper.class.getDeclaredField("SCANNER");
        field.setAccessible(true);
        field.set(null, new Scanner(in));
        financialTracker.runMainLoop();
    }

    @Test
    void runMainLoop_whenSuccessRegistration_userCanGetTransactionsStatistic() throws Exception {
        String answers = """
                2
                test5@test.ru
                123
                Ilya
                Makarov
                4.1
                4.2
                2024-12-01
                2025-12-01
                4.3
                6.1
                нет
                """;
        ByteArrayInputStream in = new ByteArrayInputStream(answers.getBytes());
        Field field = ConsoleInHelper.class.getDeclaredField("SCANNER");
        field.setAccessible(true);
        field.set(null, new Scanner(in));
        financialTracker.runMainLoop();
    }

    @Test
    void runMainLoop_whenAdminLogin_adminCanCheckUsers() throws Exception {
        String answers = """
                1
                admin
                admin
                1.1
                2.1
                нет
                """;
        ByteArrayInputStream in = new ByteArrayInputStream(answers.getBytes());
        Field field = ConsoleInHelper.class.getDeclaredField("SCANNER");
        field.setAccessible(true);
        field.set(null, new Scanner(in));
        financialTracker.runMainLoop();
    }
}