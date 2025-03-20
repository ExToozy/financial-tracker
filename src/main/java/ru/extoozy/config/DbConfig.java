package ru.extoozy.config;


import lombok.Getter;
import ru.extoozy.exception.ConfigException;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

@Getter
public class DbConfig {
    private final String driver;
    private final String changeLogFile;
    private final String url;
    private final String username;
    private final String password;
    private final String defaultSchemaName;
    private final String liquibaseSchemaName;

    public DbConfig() {
        loadProperties();
        changeLogFile = System.getProperty("changeLogFile");
        url = System.getProperty("url");
        username = System.getProperty("username");
        password = System.getProperty("password");
        defaultSchemaName = System.getProperty("defaultSchemaName");
        liquibaseSchemaName = System.getProperty("liquibaseSchemaName");
        driver = System.getProperty("driver");
    }

    private void loadProperties() {
        URL resourcesPath = this.getClass().getClassLoader().getResource("db");
        String dbPropertiesPath = resourcesPath.getPath() + "/dbConfig.properties";
        try {
            System.getProperties().load(new FileReader(dbPropertiesPath));
        } catch (IOException e) {
            throw new ConfigException("Properties could not be loaded");
        }
    }
}
