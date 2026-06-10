package lab7.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Конфигурация подключения к базе данных.
 * Приоритет: переменные окружения > application.properties > значения по умолчанию.
 */
public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final Properties props = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                props.load(input);
                logger.info("Файл application.properties загружен");
            }
        } catch (IOException e) {
            logger.warn("application.properties не найден, используются значения по умолчанию");
        }
    }

    /** @return хост БД (по умолчанию pg) */
    public static String getHost() {
        return getProperty("DB_HOST", "db.host", "pg");
    }

    /** @return порт БД (по умолчанию 5432) */
    public static int getPort() {
        return Integer.parseInt(getProperty("DB_PORT", "db.port", "5432"));
    }

    /** @return имя БД (по умолчанию studs) */
    public static String getDatabaseName() {
        return getProperty("DB_NAME", "db.name", "studs");
    }

    /** @return имя пользователя БД */
    public static String getUsername() {
        return getProperty("DB_USER", "db.user", null);
    }

    /** @return пароль БД */
    public static String getPassword() {
        return getProperty("DB_PASSWORD", "db.password", null);
    }

    private static String getProperty(String envVar, String propKey, String defaultValue) {
        String value = System.getenv(envVar);
        if (value != null && !value.isEmpty()) return value;
        value = props.getProperty(propKey);
        if (value != null && !value.isEmpty()) return value;
        return defaultValue;
    }

    /** @return полный JDBC URL для подключения к БД */
    public static String getJdbcUrl() {
        return String.format("jdbc:postgresql://%s:%d/%s",
                getHost(), getPort(), getDatabaseName());
    }
}