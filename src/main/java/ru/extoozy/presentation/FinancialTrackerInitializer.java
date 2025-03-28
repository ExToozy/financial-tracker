package ru.extoozy.presentation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.extoozy.context.ApplicationContext;
import ru.extoozy.controller.AuthController;
import ru.extoozy.controller.BudgetController;
import ru.extoozy.controller.GoalController;
import ru.extoozy.controller.TransactionController;
import ru.extoozy.controller.TransactionStatisticController;
import ru.extoozy.controller.UserController;
import ru.extoozy.controller.UserProfileController;
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

/**
 * Класс для инициализации {@link FinancialTracker}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FinancialTrackerInitializer {

    /**
     * Создаёт, инициализирует {@link FinancialTracker} и возвращает его.
     */
    public static FinancialTracker initialize() {
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

        return new FinancialTracker(new ActionManager(), new MenuManager());
    }
}
