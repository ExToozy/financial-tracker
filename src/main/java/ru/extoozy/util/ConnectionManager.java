package ru.extoozy.util;


import ru.extoozy.config.DbConfig;
import ru.extoozy.exception.ConfigException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Менеджер соединений для работы с базой данных.
 * Обеспечивает загрузку драйвера, инициализацию параметров
 * соединения и управление соединением с базой данных.
 */
public class ConnectionManager implements AutoCloseable {
    private static final DbConfig DB_CONFIG;
    private static String url;
    private static String username;
    private static String password;
    private static Connection connection;

    static {
        DB_CONFIG = new DbConfig();
        try {
            loadDriver();
        } catch (ClassNotFoundException e) {
            throw new ConfigException(e);
        }
        initFields();
    }

    private ConnectionManager() {
    }

    /**
     * Загружает драйвер базы данных.
     *
     * @throws ClassNotFoundException если драйвер не найден
     */
    private static void loadDriver() throws ClassNotFoundException {
        Class.forName(DB_CONFIG.getDriver());
    }

    /**
     * Открывает новое соединение с базой данных.
     *
     * @return объект Connection, представляющий соединение с базой данных
     * @throws SQLException если возникает ошибка при открытии соединения
     */
    public static Connection open() throws SQLException {
        connection = DriverManager.getConnection(url, username, password);
        return connection;
    }

    /**
     * Инициализирует поля конфигурации соединения.
     *
     * @throws ConfigException если не удается получить параметры конфигурации
     */
    private static void initFields() throws ConfigException {
        url = DB_CONFIG.getUrl();
        username = DB_CONFIG.getUsername();
        password = DB_CONFIG.getPassword();
    }

    /**
     * Закрывает текущее соединение с базой данных.
     *
     * @throws SQLException если возникает ошибка при закрытии соединения
     */
    @Override
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
