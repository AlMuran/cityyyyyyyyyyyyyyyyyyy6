package lab7.server.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final HikariDataSource dataSource;

    private DatabaseManager() {
        String host = "pg";
        int port = 5432;
        String database = "studs";


        String[] creds = loadCredentialsFromPgPass(host, port, database);
        String user = creds[0];
        String password = creds[1];

        if (user.isEmpty() || password.isEmpty()) {
            throw new RuntimeException("Не удалось загрузить учётные данные из .pgpass");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:postgresql://%s:%d/%s", host, port, database));
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(10);

        this.dataSource = new HikariDataSource(config);
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
        }
    }


    private static String[] loadCredentialsFromPgPass(String host, int port, String database) {
        String pgPassPath = System.getenv("PGPASSFILE");
        if (pgPassPath == null || pgPassPath.isBlank()) {
            pgPassPath = Path.of(System.getProperty("user.home"), ".pgpass").toString();
        }

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
            System.err.println("Ошибка чтения .pgpass: " + e.getMessage());
        }
        return new String[]{"", ""};
    }

    private static boolean fieldMatches(String pattern, String value) {
        return "*".equals(pattern) || pattern.equals(value);
    }
}