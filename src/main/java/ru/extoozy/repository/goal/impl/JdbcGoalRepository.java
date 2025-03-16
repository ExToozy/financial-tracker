package ru.extoozy.repository.goal.impl;

import ru.extoozy.entity.GoalEntity;
import ru.extoozy.entity.UserEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.enums.UserRole;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.repository.goal.GoalRepository;
import ru.extoozy.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcGoalRepository implements GoalRepository {

    private static final String SAVE_GOAL_SQL = """
            INSERT INTO financial_tracker_schema.goals (name, goal_amount, current_amount, user_profile_id) 
            VALUES (?, ?, ?, ?)
            """;

    private static final String UPDATE_GOAL_SQL = """
            UPDATE financial_tracker_schema.goals
            SET name = ?, goal_amount = ?, current_amount = ?
            WHERE id = ?
            """;

    private static final String FIND_ALL_GOALS_SQL = """
            SELECT g.id, g.name, g.goal_amount, g.current_amount, g.user_profile_id, 
                   u.id AS user_id, u.email, u.password, u.role, u.blocked, 
                   p.id AS profile_id, p.first_name, p.last_name 
            FROM financial_tracker_schema.goals g 
            JOIN financial_tracker_schema.user_profiles p ON g.user_profile_id = p.id
            JOIN financial_tracker_schema.users u ON p.user_id = u.id
            """;

    private static final String DELETE_GOAL_SQL = """
            DELETE FROM financial_tracker_schema.goals
            WHERE id = ?
            """;

    private static final String FIND_GOAL_BY_ID_SQL = """
            SELECT g.id, g.name, g.goal_amount, g.current_amount, g.user_profile_id, 
                   u.id AS user_id, u.email, u.password, u.role, u.blocked, 
                   p.id AS profile_id, p.first_name, p.last_name 
            FROM financial_tracker_schema.goals g 
            JOIN financial_tracker_schema.user_profiles p ON g.user_profile_id = p.id 
            JOIN financial_tracker_schema.users u ON p.user_id = u.id 
            WHERE g.id = ?
            """;

    private static final String FIND_GOALS_BY_USER_PROFILE_ID_SQL = """
            SELECT g.id, g.name, g.goal_amount, g.current_amount, g.user_profile_id,
                   u.id AS user_id, u.email, u.password, u.role, u.blocked, 
                   p.id AS profile_id, p.first_name, p.last_name 
            FROM financial_tracker_schema.goals g 
            JOIN financial_tracker_schema.user_profiles p ON g.user_profile_id = p.id 
            JOIN financial_tracker_schema.users u ON p.user_id = u.id 
            WHERE g.user_profile_id = ?
            """;

    @Override
    public void save(GoalEntity goal) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SAVE_GOAL_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, goal.getName());
            statement.setBigDecimal(2, goal.getGoalAmount());
            statement.setBigDecimal(3, goal.getCurrentAmount());
            statement.setLong(4, goal.getUserProfile().getId());  // Assuming UserProfileEntity is already populated

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        goal.setId(generatedKeys.getLong(1));
                    }
                }
            } else {
                throw new SQLException("Creating goal failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving goal", e);
        }
    }

    @Override
    public void update(GoalEntity goal) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(UPDATE_GOAL_SQL)) {

            statement.setString(1, goal.getName());
            statement.setBigDecimal(2, goal.getGoalAmount());
            statement.setBigDecimal(3, goal.getCurrentAmount());
            statement.setLong(4, goal.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new ResourceNotFoundException("Goal with id=%s not found".formatted(goal.getId()));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating goal", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(DELETE_GOAL_SQL)) {

            statement.setLong(1, id);

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting goal", e);
        }
    }

    @Override
    public List<GoalEntity> findAll() {
        List<GoalEntity> goals = new ArrayList<>();
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_GOALS_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                goals.add(mapResultSetToGoal(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all goals", e);
        }
        return goals;
    }

    @Override
    public GoalEntity findById(Long id) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(FIND_GOAL_BY_ID_SQL)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToGoal(resultSet);
                } else {
                    throw new ResourceNotFoundException("Goal with id=%s not found".formatted(id));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding goal by id", e);
        }
    }

    @Override
    public List<GoalEntity> findAllByUserProfileId(Long userProfileId) {
        List<GoalEntity> goals = new ArrayList<>();
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(FIND_GOALS_BY_USER_PROFILE_ID_SQL)) {

            statement.setLong(1, userProfileId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    goals.add(mapResultSetToGoal(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding goals by user profile id", e);
        }
        return goals;
    }

    private GoalEntity mapResultSetToGoal(ResultSet resultSet) throws SQLException {
        UserEntity user = UserEntity.builder()
                .id(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .role(UserRole.valueOf(resultSet.getString("role")))
                .blocked(resultSet.getBoolean("blocked"))
                .build();

        UserProfileEntity userProfile = new UserProfileEntity(
                resultSet.getLong("profile_id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                user
        );

        return GoalEntity.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .goalAmount(resultSet.getBigDecimal("goal_amount"))
                .currentAmount(resultSet.getBigDecimal("current_amount"))
                .userProfile(userProfile)
                .build();
    }
}
