package lab7.server.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lab7.server.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Менеджер подключения к базе данных с пулом соединений HikariCP.
 *
 * @author AlMuran
 * @version 1.0
 */
public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static DatabaseManager instance;
    private final HikariDataSource dataSource;

    private DatabaseManager() {
        String user = DatabaseConfig.getUsername();
        String password = DatabaseConfig.getPassword();


        if (password == null || password.isEmpty()) {
            logger.info("Пароль не задан в конфигурации, пробуем загрузить из .pgpass");
            String[] creds = loadCredentialsFromPgPass();
            if (creds[0] != null && !creds[0].isEmpty()) {
                user = creds[0];
                password = creds[1];
                logger.info("Учётные данные загружены из .pgpass для пользователя {}", user);
            }
        }

        if (user == null || user.isEmpty()) {
            throw new RuntimeException("Не удалось определить имя пользователя БД");
        }

        if (password == null || password.isEmpty()) {
            throw new RuntimeException("Не удалось определить пароль БД");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DatabaseConfig.getJdbcUrl());
        config.setUsername(user);
        config.setPassword(password);


        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setPoolName("CityDBPool");
        config.setConnectionTestQuery("SELECT 1");

        this.dataSource = new HikariDataSource(config);
        logger.info("Пул подключений к БД успешно инициализирован");
        checkTablesExist();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Пул подключений к БД закрыт");
        }
    }
    /**
     * Проверяет существование необходимых таблиц.
     * Если таблиц нет - выводит предупреждение с инструкцией по созданию.
     */
    private void checkTablesExist() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {


            ResultSet rsUsers = conn.getMetaData().getTables(null, null, "users", null);
            boolean usersExists = rsUsers.next();


            ResultSet rsCities = conn.getMetaData().getTables(null, null, "cities", null);
            boolean citiesExists = rsCities.next();

            if (usersExists && citiesExists) {

                ResultSet rsColumns = conn.getMetaData().getColumns(null, null, "cities", null);
                int columnCount = 0;
                while (rsColumns.next()) columnCount++;

                logger.info("Таблицы найдены: users, cities ({} колонок)", columnCount);
                logger.info("База данных готова к работе");
            } else {
                logger.warn("=== ВНИМАНИЕ: Таблицы не найдены! ===");
                logger.warn("Отсутствуют таблицы:");
                if (!usersExists) logger.warn("  - users");
                if (!citiesExists) logger.warn("  - cities");
                logger.warn("");
                logger.warn("Необходимо создать таблицы вручную одним из способов:");
                logger.warn("1. Выполнить SQL скрипт из файла init_db.sql");
                logger.warn("2. Подключиться к БД и выполнить команды CREATE TABLE");
                logger.warn("");
                logger.warn("Сервер будет работать, но команды с БД будут падать с ошибкой!");
            }

        } catch (SQLException e) {
            logger.error("Ошибка при проверке таблиц: {}", e.getMessage());
        }
    }
    /**
     * Загружает учётные данные из файла .pgpass
     * @return массив [username, password] или [null, null] если не найдено
     */
    private String[] loadCredentialsFromPgPass() {
        String pgPassPath = System.getenv("PGPASSFILE");
        if (pgPassPath == null || pgPassPath.isBlank()) {
            pgPassPath = Paths.get(System.getProperty("user.home"), ".pgpass").toString();
        }

        Path path = Paths.get(pgPassPath);
        if (!Files.exists(path)) {
            logger.warn("Файл .pgpass не найден: {}", pgPassPath);
            return new String[]{null, null};
        }

        String host = DatabaseConfig.getHost();
        int port = DatabaseConfig.getPort();
        String database = DatabaseConfig.getDatabaseName();


        try (BufferedReader reader = new BufferedReader(new FileReader(pgPassPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                String[] parts = trimmed.split(":", 5);
                if (parts.length != 5) {
                    continue;
                }

                if (fieldMatches(parts[0], host) &&
                        fieldMatches(parts[1], String.valueOf(port)) &&
                        fieldMatches(parts[2], database)) {
                    return new String[]{parts[3], parts[4]};
                }
            }
        } catch (IOException e) {
            logger.warn("Ошибка чтения .pgpass: {}", e.getMessage());
        }

        return new String[]{null, null};
    }

    private boolean fieldMatches(String pattern, String value) {
        return "*".equals(pattern) || pattern.equals(value);
    }
}