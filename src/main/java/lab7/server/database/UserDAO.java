package lab7.server.database;

import lab7.common.models.User;
import lab7.server.AuthManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

/**
 * DAO для работы с пользователями в базе данных.
 * @author AlMuran
 * @version 1.0
 */
public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    /**
     * Находит пользователя по логину.
     * @param login логин пользователя
     * @return Optional с пользователем или пустой Optional
     */
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT id, login, password_hash FROM users WHERE login = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new User(
                        rs.getLong("id"),
                        rs.getString("login"),
                        rs.getString("password_hash")
                ));
            }
        } catch (SQLException e) {
            logger.error("Ошибка поиска пользователя: {}", e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Регистрирует нового пользователя.
     * @param login логин
     * @param password пароль
     * @return true если регистрация успешна, false если пользователь уже существует
     */
    public boolean registerUser(String login, String password) {
        String hashed = AuthManager.hashPassword(password);
        String sql = "INSERT INTO users (login, password_hash) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, hashed);
            stmt.executeUpdate();
            logger.info("Зарегистрирован пользователь: {}", login);
            return true;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                logger.warn("Пользователь уже существует: {}", login);
                return false;
            }
            logger.error("Ошибка регистрации: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Проверяет логин и пароль, возвращает ID пользователя.
     * @param login логин
     * @param password пароль
     * @return ID пользователя или -1 если аутентификация не удалась
     */
    public long authenticateAndGetId(String login, String password) {
        Optional<User> user = findByLogin(login);
        if (user.isPresent()) {
            String hashed = AuthManager.hashPassword(password);
            if (hashed.equals(user.get().getPasswordHash())) {
                return user.get().getId();
            }
        }
        return -1;
    }
}