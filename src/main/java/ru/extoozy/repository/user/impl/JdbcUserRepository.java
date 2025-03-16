package ru.extoozy.repository.user.impl;

import ru.extoozy.entity.UserEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.enums.UserRole;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.repository.user.UserRepository;
import ru.extoozy.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcUserRepository implements UserRepository {

    private static final String SAVE_USER_SQL = """
            INSERT INTO financial_tracker_schema.users (email, password, role, blocked)
            VALUES (?, ?, ?, ?)
            """;

    private static final String UPDATE_USER_SQL = """
            UPDATE financial_tracker_schema.users
            SET email = ?, password = ?, role = ?, blocked = ?
            WHERE id = ?
            """;

    private static final String DELETE_USER_SQL = """
            DELETE FROM financial_tracker_schema.users
            WHERE id = ?
            """;

    private static final String FIND_ALL_USERS_SQL = """
            SELECT u.id AS user_id, u.email, u.password, u.role, u.blocked,
                   up.id AS profile_id, up.first_name, up.last_name
            FROM financial_tracker_schema.users u
            LEFT JOIN financial_tracker_schema.user_profiles up ON u.id = up.user_id
            """;

    private static final String FIND_USER_BY_ID_SQL = """
            SELECT u.id AS user_id, u.email, u.password, u.role, u.blocked,
                   up.id AS profile_id, up.first_name, up.last_name
            FROM financial_tracker_schema.users u
            LEFT JOIN financial_tracker_schema.user_profiles up ON u.id = up.user_id
            WHERE u.id = ?
            """;

    private static final String FIND_USER_BY_EMAIL_SQL = """
            SELECT u.id AS user_id, u.email, u.password, u.role, u.blocked,
                   up.id AS profile_id, up.first_name, up.last_name
            FROM financial_tracker_schema.users u
            LEFT JOIN financial_tracker_schema.user_profiles up ON u.id = up.user_id
            WHERE u.email = ?
            """;

    private static final String CHANGE_BLOCK_STATUS_SQL = """
            UPDATE financial_tracker_schema.users
            SET blocked = NOT blocked
            WHERE id = ?
            """;

    @Override
    public void save(UserEntity user) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SAVE_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getEmail());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole().toString());
            statement.setBoolean(4, user.isBlocked());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1));
                    }
                }
            } else {
                throw new SQLException("Creating user failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving user", e);
        }
    }

    @Override
    public void update(UserEntity user) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(UPDATE_USER_SQL)) {

            statement.setString(1, user.getEmail());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole().toString());
            statement.setBoolean(4, user.isBlocked());
            statement.setLong(5, user.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new ResourceNotFoundException("User with id=%s not found".formatted(user.getId()));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating user", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(DELETE_USER_SQL)) {

            statement.setLong(1, id);

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting user", e);
        }
    }

    @Override
    public List<UserEntity> findAll() {
        List<UserEntity> users = new ArrayList<>();
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_USERS_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all users", e);
        }
        return users;
    }

    @Override
    public UserEntity findById(Long id) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(FIND_USER_BY_ID_SQL)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                } else {
                    throw new ResourceNotFoundException("User with id=%s not found".formatted(id));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding user by id", e);
        }
    }

    @Override
    public UserEntity getByEmail(String email) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(FIND_USER_BY_EMAIL_SQL)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                } else {
                    throw new ResourceNotFoundException("User with email=%s not found".formatted(email));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding user by email", e);
        }
    }

    @Override
    public void changeBlockStatus(Long id) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(CHANGE_BLOCK_STATUS_SQL)) {

            statement.setLong(1, id);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new ResourceNotFoundException("User with id=%s not found".formatted(id));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while changing block status", e);
        }
    }

    private UserEntity mapResultSetToUser(ResultSet resultSet) throws SQLException {
        Long userId = resultSet.getLong("user_id");
        String email = resultSet.getString("email");
        String password = resultSet.getString("password");
        UserRole role = resultSet.getString("role") != null ? UserRole.valueOf(resultSet.getString("role")) : null;
        boolean blocked = resultSet.getBoolean("blocked");

        Long profileId = resultSet.getLong("profile_id");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");

        UserProfileEntity userProfile = new UserProfileEntity(profileId, firstName, lastName, null);

        UserEntity user = UserEntity.builder()
                .id(userId)
                .email(email)
                .password(password)
                .role(role)
                .userProfile(userProfile)
                .blocked(blocked)
                .build();

        userProfile.setUser(user);

        return user;
    }
}
