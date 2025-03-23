package ru.extoozy.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.extoozy.context.ApplicationContext;
import ru.extoozy.migration.MigrationTool;
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

@WebListener
public class ApplicationContextInitializer implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        MigrationTool.runMigrate();

        EmailService emailService = new ConsoleMockEmailService();

        BudgetRepository budgetRepository = new JdbcBudgetRepository();
        BudgetService budgetService = new BudgetServiceImpl(budgetRepository);

        UserProfileRepository userProfileRepository = new JdbcUserProfileRepository();
        UserProfileService userProfileService = new UserProfileServiceImpl(userProfileRepository);

        TransactionRepository transactionRepository = new JdbcTransactionRepository();
        TransactionService transactionService = new TransactionServiceImpl(transactionRepository, budgetRepository, emailService);

        GoalRepository goalRepository = new JdbcGoalRepository();
        GoalService goalService = new GoalServiceImpl(goalRepository);

        UserRepository userRepository = new JdbcUserRepository();
        UserService userService = new UserServiceImpl(userRepository);

        AuthService authService = new AuthServiceImpl(userService, userRepository);

        TransactionStatisticService transactionStatisticService = new TransactionStatisticServiceImpl(transactionRepository);

        ApplicationContext.addBean(UserRepository.class, userRepository);
        ApplicationContext.addBean(AuthService.class, authService);
        ApplicationContext.addBean(UserService.class, userService);
        ApplicationContext.addBean(GoalService.class, goalService);
        ApplicationContext.addBean(TransactionService.class, transactionService);
        ApplicationContext.addBean(UserProfileService.class, userProfileService);
        ApplicationContext.addBean(BudgetService.class, budgetService);
        ApplicationContext.addBean(AuthService.class, authService);
        ApplicationContext.addBean(TransactionStatisticService.class, transactionStatisticService);
        ApplicationContext.addBean(EmailService.class, emailService);
    }
}
