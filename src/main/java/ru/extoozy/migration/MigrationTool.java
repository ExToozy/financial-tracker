package ru.extoozy.migration;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import ru.extoozy.config.DbConfig;
import ru.extoozy.exception.ConfigException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MigrationTool {
    public static void main(String[] args) {
        runMigrate();
    }

    public static void runMigrate() {
        DbConfig dbConfig = new DbConfig();
        try (Connection connection = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(),
                dbConfig.getPassword())) {
            createSchemas(connection, dbConfig);
            migrateDatabase(connection, dbConfig);
        } catch (SQLException | LiquibaseException e) {
            throw new ConfigException(e);
        }
    }

    private static void createSchemas(Connection connection, DbConfig dbConfig) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + dbConfig.getLiquibaseSchemaName());
            statement.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + dbConfig.getDefaultSchemaName());
        }
    }

    private static void migrateDatabase(Connection connection, DbConfig dbConfig) throws LiquibaseException {
        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase = new Liquibase(dbConfig.getChangeLogFile(), new ClassLoaderResourceAccessor(), database);
        database.setLiquibaseSchemaName(dbConfig.getLiquibaseSchemaName());
        database.setDefaultSchemaName(dbConfig.getDefaultSchemaName());
        liquibase.update();
        liquibase.close();
    }
}
