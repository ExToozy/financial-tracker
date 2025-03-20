package ru.extoozy.repository.budget.impl;

import lombok.extern.slf4j.Slf4j;
import ru.extoozy.entity.BudgetEntity;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.enums.UserRole;
import ru.extoozy.exception.RepositoryException;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.repository.budget.BudgetRepository;
import ru.extoozy.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JdbcBudgetRepository implements BudgetRepository {

    private static final String INSERT_BUDGET = "INSERT INTO financial_tracker_schema.budgets (period, max_amount, current_amount, user_profile_id) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_BUDGET = "UPDATE financial_tracker_schema.budgets SET max_amount = ?, current_amount = ? WHERE id = ?";
    private static final String DELETE_BUDGET = "DELETE FROM financial_tracker_schema.budgets WHERE id = ?";
    private static final String SELECT_ALL_BUDGETS = """
            SELECT b.*, up.id AS up_id, up.first_name, up.last_name,
                   u.id AS u_id, u.email, u.password, u.role, u.blocked
            FROM financial_tracker_schema.budgets b
            JOIN financial_tracker_schema.user_profiles up ON b.user_profile_id = up.id
            JOIN financial_tracker_schema.users u ON up.user_id = u.id
            """;

    private static final String SELECT_BUDGET_BY_ID = """
            SELECT b.*, up.id AS up_id, up.first_name, up.last_name, 
                   u.id AS u_id, u.email, u.password, u.role, u.blocked
            FROM financial_tracker_schema.budgets b
            JOIN financial_tracker_schema.user_profiles up ON b.user_profile_id = up.id
            JOIN financial_tracker_schema.users u ON up.user_id = u.id
            WHERE b.id = ?
            """;

    private static final String SELECT_BUDGET_BY_USER_PROFILE_ID = """
            SELECT b.*, up.id AS up_id, up.first_name, up.last_name, 
                   u.id AS u_id, u.email, u.password, u.role, u.blocked
            FROM financial_tracker_schema.budgets b
            JOIN financial_tracker_schema.user_profiles up ON b.user_profile_id = up.id
            JOIN financial_tracker_schema.users u ON up.user_id = u.id
            WHERE b.user_profile_id = ?
            """;

    private static final String SELECT_BUDGET_BY_USER_PROFILE_ID_AND_CURRENT_MONTH = """
            SELECT b.*, up.id AS up_id, up.first_name, up.last_name, 
                   u.id AS u_id, u.email, u.password, u.role, u.blocked
            FROM financial_tracker_schema.budgets b
            JOIN financial_tracker_schema.user_profiles up ON b.user_profile_id = up.id
            JOIN financial_tracker_schema.users u ON up.user_id = u.id
            WHERE b.user_profile_id = ? AND b.period = ?
            """;

    @Override
    public void save(BudgetEntity budget) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(INSERT_BUDGET, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, budget.getPeriod().toString());
            statement.setBigDecimal(2, budget.getMaxAmount());
            statement.setBigDecimal(3, budget.getCurrentAmount());
            statement.setLong(4, budget.getUserProfile().getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        budget.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error while saving budget", e);
        }
    }

    @Override
    public void update(BudgetEntity budget) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BUDGET)) {

            statement.setBigDecimal(1, budget.getMaxAmount());
            statement.setBigDecimal(2, budget.getCurrentAmount());
            statement.setLong(3, budget.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new ResourceNotFoundException("Budget with id=%s not found".formatted(budget.getId()));
            }
        } catch (SQLException e) {
            log.error("Error while updating budget", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(DELETE_BUDGET)) {

            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            log.error("Error while deleting budget", e);
            return false;
        }
    }

    @Override
    public List<BudgetEntity> findAll() {
        List<BudgetEntity> budgets = new ArrayList<>();
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_BUDGETS);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                budgets.add(mapResultSetToBudget(resultSet));
            }
        } catch (SQLException e) {
            log.error("Error while fetching all budgets", e);
        }
        return budgets;
    }

    @Override
    public BudgetEntity findById(Long id) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SELECT_BUDGET_BY_ID)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToBudget(resultSet);
                } else {
                    throw new ResourceNotFoundException("Budget with id=%s not found".formatted(id));
                }
            }
        } catch (SQLException e) {
            log.error("Error while fetching budget by id", e);
            throw new RepositoryException("Error while fetching budget by id=%s".formatted(id));
        }
    }

    @Override
    public List<BudgetEntity> findAllByUserProfileId(Long id) {
        List<BudgetEntity> budgets = new ArrayList<>();
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SELECT_BUDGET_BY_USER_PROFILE_ID)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    budgets.add(mapResultSetToBudget(resultSet));
                }
            }
        } catch (SQLException e) {
            log.error("Error while fetching budgets by user profile id", e);
        }
        return budgets;
    }

    @Override
    public BudgetEntity findByUserProfileIdAndCurrentMonth(Long id) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SELECT_BUDGET_BY_USER_PROFILE_ID_AND_CURRENT_MONTH)) {

            statement.setLong(1, id);
            statement.setString(2, YearMonth.now().toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToBudget(resultSet);
                } else {
                    throw new ResourceNotFoundException(
                            "Budget with user profile id=%s and period=%s not found".formatted(id, YearMonth.now())
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching budget by user profile id and current month", e);
        }
    }

    private BudgetEntity mapResultSetToBudget(ResultSet resultSet) throws SQLException {
        UserEntity userEntity = UserEntity.builder()
                .id(resultSet.getLong("u_id"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .role(UserRole.valueOf(resultSet.getString("role")))
                .blocked(resultSet.getBoolean("blocked"))
                .build();

        UserProfileEntity userProfileEntity = UserProfileEntity.builder()
                .id(resultSet.getLong("up_id"))
                .firstName(resultSet.getString("first_name"))
                .lastName(resultSet.getString("last_name"))
                .user(userEntity)
                .build();

        return BudgetEntity.builder()
                .id(resultSet.getLong("id"))
                .period(YearMonth.parse(resultSet.getString("period")))
                .maxAmount(resultSet.getBigDecimal("max_amount"))
                .currentAmount(resultSet.getBigDecimal("current_amount"))
                .userProfile(userProfileEntity)
                .build();
    }
}
