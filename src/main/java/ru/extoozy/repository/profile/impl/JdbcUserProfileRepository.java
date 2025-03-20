package ru.extoozy.repository.profile.impl;

import ru.extoozy.entity.UserEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.enums.UserRole;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.repository.profile.UserProfileRepository;
import ru.extoozy.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcUserProfileRepository implements UserProfileRepository {

    private static final String SAVE_USER_PROFILE_SQL = """
            INSERT INTO financial_tracker_schema.user_profiles (first_name, last_name, user_id)
            VALUES (?, ?, ?)
            """;

    private static final String UPDATE_USER_PROFILE_SQL = """
            UPDATE financial_tracker_schema.user_profiles
            SET first_name = ?, last_name = ?
            WHERE id = ?
            """;

    private static final String DELETE_USER_PROFILE_SQL = """
            DELETE FROM financial_tracker_schema.user_profiles
            WHERE id = ?
            """;

    private static final String FIND_ALL_USER_PROFILES_SQL = """
            SELECT up.id AS profile_id, up.first_name, up.last_name, up.user_id,
                   u.id AS user_id, u.email, u.password, u.role, u.blocked
            FROM financial_tracker_schema.user_profiles up
            JOIN financial_tracker_schema.users u ON up.user_id = u.id
            """;

    private static final String FIND_USER_PROFILE_BY_ID_SQL = """
            SELECT up.id AS profile_id, up.first_name, up.last_name, up.user_id,
                   u.id AS user_id, u.email, u.password, u.role, u.blocked
            FROM financial_tracker_schema.user_profiles up
            JOIN financial_tracker_schema.users u ON up.user_id = u.id
            WHERE up.id = ?
            """;

    private static final String FIND_USER_PROFILE_BY_USER_ID_SQL = """
            SELECT up.id AS profile_id, up.first_name, up.last_name, up.user_id,
                   u.id AS user_id, u.email, u.password, u.role, u.blocked
            FROM financial_tracker_schema.user_profiles up
            JOIN financial_tracker_schema.users u ON up.user_id = u.id
            WHERE up.user_id = ?
            """;


    @Override
    public void save(UserProfileEntity userProfile) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SAVE_USER_PROFILE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, userProfile.getFirstName());
            statement.setString(2, userProfile.getLastName());
            statement.setLong(3, userProfile.getUser().getId());  // Assuming UserEntity is already populated

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userProfile.setId(generatedKeys.getLong(1));
                    }
                }
            } else {
                throw new SQLException("Creating user profile failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving user profile", e);
        }
    }

    @Override
    public void update(UserProfileEntity userProfile) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(UPDATE_USER_PROFILE_SQL)) {

            statement.setString(1, userProfile.getFirstName());
            statement.setString(2, userProfile.getLastName());
            statement.setLong(3, userProfile.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new ResourceNotFoundException("User profile with id=%s not found".formatted(userProfile.getId()));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating user profile", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(DELETE_USER_PROFILE_SQL)) {

            statement.setLong(1, id);

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting user profile", e);
        }
    }

    @Override
    public List<UserProfileEntity> findAll() {
        List<UserProfileEntity> userProfiles = new ArrayList<>();
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_USER_PROFILES_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                userProfiles.add(mapResultSetToUserProfile(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all user profiles", e);
        }
        return userProfiles;
    }

    @Override
    public UserProfileEntity findById(Long id) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(FIND_USER_PROFILE_BY_ID_SQL)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUserProfile(resultSet);
                } else {
                    throw new ResourceNotFoundException("User profile with id=%s not found".formatted(id));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding user profile by id", e);
        }
    }

    @Override
    public UserProfileEntity findByUserId(Long userId) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(FIND_USER_PROFILE_BY_USER_ID_SQL)) {

            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUserProfile(resultSet);
                } else {
                    throw new ResourceNotFoundException("User profile with user id=%s not found".formatted(userId));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding user profile by user id", e);
        }
    }

    private UserProfileEntity mapResultSetToUserProfile(ResultSet resultSet) throws SQLException {
        UserEntity userEntity = UserEntity.builder()
                .id(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .role(UserRole.valueOf(resultSet.getString("role")))
                .blocked(resultSet.getBoolean("blocked"))
                .build();

        UserProfileEntity userProfileEntity = UserProfileEntity.builder()
                .id(resultSet.getLong("profile_id"))
                .firstName(resultSet.getString("first_name"))
                .lastName(resultSet.getString("last_name"))
                .user(userEntity)
                .build();

        return userProfileEntity;
    }
}
