package ru.extoozy.repository.transaction.impl;

import ru.extoozy.entity.TransactionEntity;
import ru.extoozy.entity.UserProfileEntity;
import ru.extoozy.exception.ResourceNotFoundException;
import ru.extoozy.repository.transaction.TransactionRepository;
import ru.extoozy.util.ConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcTransactionRepository implements TransactionRepository {

    private static final String SAVE_TRANSACTION_SQL = """
            INSERT INTO financial_tracker_schema.transactions (amount, category, description, user_profile_id, transaction_type)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_TRANSACTION_SQL = """
            UPDATE financial_tracker_schema.transactions
            SET amount = ?, category = ?, description = ?
            WHERE id = ?
            """;

    private static final String DELETE_TRANSACTION_SQL = """
            DELETE FROM financial_tracker_schema.transactions
            WHERE id = ?
            """;

    private static final String FIND_ALL_TRANSACTIONS_SQL = """
            SELECT t.id AS transaction_id, t.amount, t.category, t.description, t.user_profile_id, 
                   up.id AS profile_id, up.first_name, up.last_name 
            FROM financial_tracker_schema.transactions t 
            JOIN financial_tracker_schema.user_profiles up ON t.user_profile_id = up.id
            """;

    private static final String FIND_TRANSACTION_BY_ID_SQL = """
            SELECT t.id AS transaction_id, t.amount, t.category, t.description, t.user_profile_id, 
                   up.id AS profile_id, up.first_name, up.last_name 
            FROM financial_tracker_schema.transactions t 
            JOIN financial_tracker_schema.user_profiles up ON t.user_profile_id = up.id 
            WHERE t.id = ?
            """;

    private static final String FIND_TRANSACTIONS_BY_USER_PROFILE_ID_SQL = """
            SELECT t.id AS transaction_id, t.amount, t.category, t.description, t.user_profile_id, 
                   up.id AS profile_id, up.first_name, up.last_name 
            FROM financial_tracker_schema.transactions t 
            JOIN financial_tracker_schema.user_profiles up ON t.user_profile_id = up.id 
            WHERE t.user_profile_id = ?
            """;

    @Override
    public void save(TransactionEntity transaction) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SAVE_TRANSACTION_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setBigDecimal(1, transaction.getAmount());
            statement.setString(2, transaction.getCategory());
            statement.setString(3, transaction.getDescription());
            statement.setLong(4, transaction.getUserProfile().getId());
            statement.setString(5, transaction.getTransactionType().toString());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transaction.setId(generatedKeys.getLong(1));
                    }
                }
            } else {
                throw new SQLException("Creating transaction failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving transaction", e);
        }
    }

    @Override
    public void update(TransactionEntity transaction) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(UPDATE_TRANSACTION_SQL)) {

            statement.setBigDecimal(1, transaction.getAmount());
            statement.setString(2, transaction.getCategory());
            statement.setString(3, transaction.getDescription());
            statement.setLong(4, transaction.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new ResourceNotFoundException("Transaction with id=%s not found".formatted(transaction.getId()));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating transaction", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(DELETE_TRANSACTION_SQL)) {

            statement.setLong(1, id);

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting transaction", e);
        }
    }

    @Override
    public List<TransactionEntity> findAll() {
        List<TransactionEntity> transactions = new ArrayList<>();
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_TRANSACTIONS_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                transactions.add(mapResultSetToTransaction(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all transactions", e);
        }
        return transactions;
    }

    @Override
    public TransactionEntity findById(Long id) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(FIND_TRANSACTION_BY_ID_SQL)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToTransaction(resultSet);
                } else {
                    throw new ResourceNotFoundException("Transaction with id=%s not found".formatted(id));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding transaction by id", e);
        }
    }

    @Override
    public List<TransactionEntity> findAllByUserProfileId(Long id) {
        List<TransactionEntity> transactions = new ArrayList<>();
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(FIND_TRANSACTIONS_BY_USER_PROFILE_ID_SQL)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    transactions.add(mapResultSetToTransaction(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding transactions by user profile id", e);
        }
        return transactions;
    }

    private TransactionEntity mapResultSetToTransaction(ResultSet resultSet) throws SQLException {
        Long transactionId = resultSet.getLong("transaction_id");
        double amount = resultSet.getDouble("amount");
        String category = resultSet.getString("category");
        String description = resultSet.getString("description");

        Long profileId = resultSet.getLong("profile_id");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");

        UserProfileEntity userProfile = new UserProfileEntity(profileId, firstName, lastName, null); // Assuming 'user' is not yet populated

        return TransactionEntity.builder()
                .id(transactionId)
                .amount(BigDecimal.valueOf(amount))
                .category(category)
                .description(description)
                .userProfile(userProfile)
                .build();
    }
}
